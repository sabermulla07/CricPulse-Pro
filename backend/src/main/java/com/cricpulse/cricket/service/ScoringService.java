package com.cricpulse.cricket.service;

import com.cricpulse.cricket.dto.BallRecordRequest;
import com.cricpulse.cricket.dto.MatchDetailDTO;
import com.cricpulse.cricket.model.*;
import com.cricpulse.cricket.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class ScoringService {

    private final MatchRepository matchRepository;
    private final MatchInningsRepository inningsRepository;
    private final BattingScorecardRepository battingRepository;
    private final BowlingScorecardRepository bowlingRepository;
    private final BallEventRepository ballEventRepository;
    private final FallOfWicketRepository fowRepository;
    private final PlayerRepository playerRepository;
    private final MatchService matchService;
    private final LiveStreamNotifier liveStreamNotifier;
    private final Random random = new Random();

    public ScoringService(MatchRepository matchRepository,
                          MatchInningsRepository inningsRepository,
                          BattingScorecardRepository battingRepository,
                          BowlingScorecardRepository bowlingRepository,
                          BallEventRepository ballEventRepository,
                          FallOfWicketRepository fowRepository,
                          PlayerRepository playerRepository,
                          MatchService matchService,
                          LiveStreamNotifier liveStreamNotifier) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.battingRepository = battingRepository;
        this.bowlingRepository = bowlingRepository;
        this.ballEventRepository = ballEventRepository;
        this.fowRepository = fowRepository;
        this.playerRepository = playerRepository;
        this.matchService = matchService;
        this.liveStreamNotifier = liveStreamNotifier;
    }

    public MatchDetailDTO recordBall(Long matchId, BallRecordRequest req) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.ABANDONED) {
            return matchService.getMatchDetails(matchId);
        }

        if (match.getStatus() == MatchStatus.UPCOMING) {
            match.setStatus(MatchStatus.LIVE);
        }

        // Get current innings
        MatchInnings innings = inningsRepository.findByMatchAndInningsNumber(match, match.getCurrentInnings())
                .orElseThrow(() -> new RuntimeException("Innings " + match.getCurrentInnings() + " not found for match " + matchId));

        if (Boolean.TRUE.equals(innings.getIsCompleted())) {
            return matchService.getMatchDetails(matchId);
        }

        // Find active striker and non-striker
        List<BattingScorecard> onCrease = battingRepository.findByInningsAndIsOnCreaseTrue(innings);
        BattingScorecard striker = null;
        BattingScorecard nonStriker = null;

        for (BattingScorecard b : onCrease) {
            if (Boolean.TRUE.equals(b.getIsStriker())) {
                striker = b;
            } else {
                nonStriker = b;
            }
        }

        if (striker == null && !onCrease.isEmpty()) {
            striker = onCrease.get(0);
            striker.setIsStriker(true);
        }
        if (nonStriker == null && onCrease.size() > 1) {
            nonStriker = onCrease.get(1);
            nonStriker.setIsStriker(false);
        }

        // Find current bowler
        BowlingScorecard bowler = bowlingRepository.findByInningsAndIsCurrentBowlerTrue(innings)
                .orElse(null);

        if (bowler == null) {
            List<BowlingScorecard> bowlers = bowlingRepository.findByInningsOrderByBowlingOrderAsc(innings);
            if (!bowlers.isEmpty()) {
                bowler = bowlers.get(0);
                bowler.setIsCurrentBowler(true);
            }
        }

        int runsScored = req.getRuns() != null ? req.getRuns() : 0;
        boolean isWicket = Boolean.TRUE.equals(req.getIsWicket());
        boolean isExtra = Boolean.TRUE.equals(req.getIsExtra());
        String extraType = req.getExtraType();
        int extraRuns = req.getExtraRuns() != null ? req.getExtraRuns() : 0;

        boolean isLegalBall = true;
        if (isExtra && ("WIDE".equalsIgnoreCase(extraType) || "NO_BALL".equalsIgnoreCase(extraType))) {
            isLegalBall = false;
        }

        int totalBallRuns = runsScored + extraRuns;
        if (isExtra && extraRuns == 0) {
            totalBallRuns += 1;
            extraRuns = 1;
        }

        // Update innings totals
        innings.setTotalRuns(innings.getTotalRuns() + totalBallRuns);
        if (isExtra) {
            innings.setExtras(innings.getExtras() + extraRuns);
            if ("WIDE".equalsIgnoreCase(extraType)) {
                innings.setWides(innings.getWides() + extraRuns);
            } else if ("NO_BALL".equalsIgnoreCase(extraType)) {
                innings.setNoBalls(innings.getNoBalls() + extraRuns);
            } else if ("BYE".equalsIgnoreCase(extraType)) {
                innings.setByes(innings.getByes() + extraRuns);
            } else if ("LEG_BYE".equalsIgnoreCase(extraType)) {
                innings.setLegByes(innings.getLegByes() + extraRuns);
            }
        }

        if (isLegalBall) {
            innings.setLegalBalls(innings.getLegalBalls() + 1);
            innings.setOversBowled(calculateOversDecimal(innings.getLegalBalls()));
        }

        innings.setCurrentRunRate(MatchService.calculateRunRate(innings.getTotalRuns(), innings.getLegalBalls()));

        // Calculate RRR for 2nd innings
        if (match.getCurrentInnings() == 2 && match.getTarget() != null && match.getTarget() > 0) {
            int runsNeeded = match.getTarget() - innings.getTotalRuns();
            int totalBalls = match.getTotalOvers() * 6;
            int ballsLeft = Math.max(0, totalBalls - innings.getLegalBalls());
            innings.setRequiredRunRate(ballsLeft > 0 ? MatchService.calculateRunRate(runsNeeded, ballsLeft) : 0.0);
        }

        // Update Batsman
        if (striker != null) {
            if (isLegalBall || (!"WIDE".equalsIgnoreCase(extraType))) {
                striker.setBalls(striker.getBalls() + 1);
            }
            if (!isExtra || "NO_BALL".equalsIgnoreCase(extraType)) {
                striker.setRuns(striker.getRuns() + runsScored);
                if (runsScored == 4) striker.setFours(striker.getFours() + 1);
                if (runsScored == 6) striker.setSixes(striker.getSixes() + 1);
            }
            striker.setStrikeRate(striker.getBalls() > 0 ?
                    Math.round(((double) striker.getRuns() / striker.getBalls() * 100.0) * 100.0) / 100.0 : 0.0);
        }

        // Update Bowler
        if (bowler != null) {
            if (isLegalBall) {
                bowler.setLegalBalls(bowler.getLegalBalls() + 1);
                bowler.setOvers(calculateOversDecimal(bowler.getLegalBalls()));
            }
            // Byes and leg byes don't count against bowler runs
            if (!("BYE".equalsIgnoreCase(extraType) || "LEG_BYE".equalsIgnoreCase(extraType))) {
                bowler.setRunsConceded(bowler.getRunsConceded() + totalBallRuns);
            }
            if (isLegalBall && runsScored == 0 && !isExtra) {
                bowler.setDots(bowler.getDots() + 1);
            }
            if (runsScored == 4) bowler.setFoursConceded(bowler.getFoursConceded() + 1);
            if (runsScored == 6) bowler.setSixesConceded(bowler.getSixesConceded() + 1);
            if ("WIDE".equalsIgnoreCase(extraType)) bowler.setWides(bowler.getWides() + extraRuns);
            if ("NO_BALL".equalsIgnoreCase(extraType)) bowler.setNoBalls(bowler.getNoBalls() + 1);

            bowler.setEconomy(bowler.getLegalBalls() > 0 ?
                    Math.round(((double) bowler.getRunsConceded() / bowler.getLegalBalls() * 6.0) * 100.0) / 100.0 : 0.0);
        }

        // Handle Wickets
        Player outPlayer = null;
        if (isWicket && striker != null) {
            innings.setTotalWickets(innings.getTotalWickets() + 1);
            if (bowler != null && !"RUN_OUT".equalsIgnoreCase(req.getWicketType())) {
                bowler.setWickets(bowler.getWickets() + 1);
            }

            String wType = req.getWicketType() != null ? req.getWicketType() : "CAUGHT";
            striker.setIsOut(true);
            striker.setIsOnCrease(false);
            striker.setIsStriker(false);
            striker.setDismissalType(wType);
            striker.setBowler(bowler != null ? bowler.getPlayer() : null);

            String dismissalText;
            if ("BOWLED".equalsIgnoreCase(wType)) {
                dismissalText = "b " + (bowler != null ? bowler.getPlayer().getName() : "Bowler");
            } else if ("LBW".equalsIgnoreCase(wType)) {
                dismissalText = "lbw b " + (bowler != null ? bowler.getPlayer().getName() : "Bowler");
            } else if ("RUN_OUT".equalsIgnoreCase(wType)) {
                dismissalText = "run out";
            } else if ("STUMPED".equalsIgnoreCase(wType)) {
                dismissalText = "st †Keeper b " + (bowler != null ? bowler.getPlayer().getName() : "Bowler");
            } else {
                dismissalText = "c Fielder b " + (bowler != null ? bowler.getPlayer().getName() : "Bowler");
            }
            striker.setDismissal(dismissalText);
            outPlayer = striker.getPlayer();

            // Record Fall of Wicket
            FallOfWicket fow = new FallOfWicket(
                    innings,
                    innings.getTotalWickets(),
                    striker.getPlayer(),
                    innings.getTotalRuns(),
                    MatchService.formatOvers(innings.getLegalBalls())
            );
            fowRepository.save(fow);

            // Bring next batsman in if available and wickets < 10
            if (innings.getTotalWickets() < 10) {
                Optional<BattingScorecard> nextBatterOpt = battingRepository
                        .findFirstByInningsAndIsOnCreaseFalseAndIsOutFalseOrderByBattingOrderAsc(innings);
                if (nextBatterOpt.isPresent()) {
                    BattingScorecard nextBatter = nextBatterOpt.get();
                    nextBatter.setIsOnCrease(true);
                    nextBatter.setIsStriker(true);
                    battingRepository.save(nextBatter);
                    striker = nextBatter;
                }
            }
        }

        // Strike rotation on odd runs (1 or 3 runs) if no wicket
        if (!isWicket && (runsScored == 1 || runsScored == 3) && striker != null && nonStriker != null) {
            striker.setIsStriker(false);
            nonStriker.setIsStriker(true);
            // swap reference for next checks
            BattingScorecard temp = striker;
            striker = nonStriker;
            nonStriker = temp;
        }

        // End of Over handling
        boolean isOverCompleted = isLegalBall && (innings.getLegalBalls() % 6 == 0);
        if (isOverCompleted) {
            // Batsmen swap strike at end of over
            if (striker != null && nonStriker != null) {
                striker.setIsStriker(false);
                nonStriker.setIsStriker(true);
            }

            // Rotate bowler: pick another bowler from bowling scorecard
            if (bowler != null) {
                bowler.setIsCurrentBowler(false);
                bowlingRepository.save(bowler);

                List<BowlingScorecard> allBowlers = bowlingRepository.findByInningsOrderByBowlingOrderAsc(innings);
                BowlingScorecard nextBowler = null;
                for (BowlingScorecard b : allBowlers) {
                    if (!b.getId().equals(bowler.getId())) {
                        nextBowler = b;
                        break;
                    }
                }
                if (nextBowler != null) {
                    nextBowler.setIsCurrentBowler(true);
                    bowlingRepository.save(nextBowler);
                }
            }
        }

        // Save batting & bowling updates
        if (striker != null) battingRepository.save(striker);
        if (nonStriker != null) battingRepository.save(nonStriker);
        if (bowler != null) bowlingRepository.save(bowler);
        inningsRepository.save(innings);

        // Build BallEvent & Commentary
        int overNum = (innings.getLegalBalls() - (isLegalBall ? 1 : 0)) / 6;
        int ballInOver = isLegalBall ? ((innings.getLegalBalls() - 1) % 6 + 1) : (innings.getLegalBalls() % 6);
        String ballDisplay = overNum + "." + ballInOver;

        String commentaryText = req.getCustomCommentary();
        if (commentaryText == null || commentaryText.trim().isEmpty()) {
            commentaryText = generateCommentary(
                    striker != null ? striker.getPlayer().getName() : "Batsman",
                    bowler != null ? bowler.getPlayer().getName() : "Bowler",
                    runsScored,
                    isWicket,
                    req.getWicketType(),
                    isExtra,
                    extraType
            );
        }

        BallEvent ballEvent = new BallEvent();
        ballEvent.setMatch(match);
        ballEvent.setInnings(innings);
        ballEvent.setOverNumber(overNum);
        ballEvent.setBallNumberInOver(ballInOver);
        ballEvent.setBallDisplay(ballDisplay);
        ballEvent.setBatsman(striker != null ? striker.getPlayer() : null);
        ballEvent.setNonStriker(nonStriker != null ? nonStriker.getPlayer() : null);
        ballEvent.setBowler(bowler != null ? bowler.getPlayer() : null);
        ballEvent.setRunsScored(runsScored);
        ballEvent.setIsWicket(isWicket);
        ballEvent.setWicketType(req.getWicketType());
        ballEvent.setOutPlayer(outPlayer);
        ballEvent.setIsExtra(isExtra);
        ballEvent.setExtraType(extraType);
        ballEvent.setExtraRuns(extraRuns);
        ballEvent.setTotalBallRuns(totalBallRuns);
        ballEvent.setCommentary(commentaryText);
        ballEvent.setScoreAfterBall(innings.getTotalRuns() + "/" + innings.getTotalWickets());
        ballEvent.setTimestamp(LocalDateTime.now());
        ballEventRepository.save(ballEvent);

        // Check Innings and Match Completion
        checkMatchProgression(match, innings);

        match.setUpdatedAt(LocalDateTime.now());
        matchRepository.save(match);

        // Prepare updated state & broadcast via SSE
        MatchDetailDTO updatedDetails = matchService.getMatchDetails(matchId);
        liveStreamNotifier.broadcast(matchId, updatedDetails);

        return updatedDetails;
    }

    private void checkMatchProgression(Match match, MatchInnings innings) {
        int maxLegalBalls = match.getTotalOvers() * 6;
        boolean allOut = innings.getTotalWickets() >= 10;
        boolean oversFinished = innings.getLegalBalls() >= maxLegalBalls;

        if (match.getCurrentInnings() == 1) {
            if (allOut || oversFinished) {
                innings.setIsCompleted(true);
                inningsRepository.save(innings);

                match.setTarget(innings.getTotalRuns() + 1);
                match.setCurrentInnings(2);
                match.setStatus(MatchStatus.LIVE);

                // Start 2nd innings
                startSecondInnings(match, innings);
            }
        } else if (match.getCurrentInnings() == 2) {
            int target = match.getTarget() != null ? match.getTarget() : 0;
            boolean chaseCompleted = innings.getTotalRuns() >= target;

            if (chaseCompleted) {
                innings.setIsCompleted(true);
                inningsRepository.save(innings);
                match.setStatus(MatchStatus.COMPLETED);
                match.setIsSimulationActive(false);
                int wicketsLeft = 10 - innings.getTotalWickets();
                match.setResultSummary(innings.getBattingTeam().getName() + " won by " + wicketsLeft + " wickets!");
                assignPlayerOfTheMatch(match);
            } else if (allOut || oversFinished) {
                innings.setIsCompleted(true);
                inningsRepository.save(innings);
                match.setStatus(MatchStatus.COMPLETED);
                match.setIsSimulationActive(false);

                int runsShort = (target - 1) - innings.getTotalRuns();
                if (runsShort == 0) {
                    match.setResultSummary("Match Tied! Dramatic finish!");
                } else {
                    match.setResultSummary(innings.getBowlingTeam().getName() + " won by " + runsShort + " runs!");
                }
                assignPlayerOfTheMatch(match);
            }
        }
    }

    private void startSecondInnings(Match match, MatchInnings firstInnings) {
        Optional<MatchInnings> inn2Opt = inningsRepository.findByMatchAndInningsNumber(match, 2);
        MatchInnings inn2;
        if (inn2Opt.isPresent()) {
            inn2 = inn2Opt.get();
        } else {
            inn2 = new MatchInnings();
            inn2.setMatch(match);
            inn2.setInningsNumber(2);
            inn2.setBattingTeam(firstInnings.getBowlingTeam());
            inn2.setBowlingTeam(firstInnings.getBattingTeam());
            inn2 = inningsRepository.save(inn2);

            // Populate batting scorecard for second innings
            List<Player> batters = playerRepository.findByTeam(inn2.getBattingTeam());
            int order = 1;
            for (Player p : batters) {
                BattingScorecard bs = new BattingScorecard();
                bs.setInnings(inn2);
                bs.setPlayer(p);
                bs.setBattingOrder(order);
                if (order == 1) {
                    bs.setIsOnCrease(true);
                    bs.setIsStriker(true);
                } else if (order == 2) {
                    bs.setIsOnCrease(true);
                    bs.setIsStriker(false);
                }
                battingRepository.save(bs);
                order++;
                if (order > 11) break;
            }

            // Populate bowling scorecard for second innings
            List<Player> bowlers = playerRepository.findByTeam(inn2.getBowlingTeam());
            int bowOrder = 1;
            for (Player p : bowlers) {
                if (p.getRole() == PlayerRole.BOWLER || p.getRole() == PlayerRole.ALL_ROUNDER) {
                    BowlingScorecard bow = new BowlingScorecard();
                    bow.setInnings(inn2);
                    bow.setPlayer(p);
                    bow.setBowlingOrder(bowOrder);
                    if (bowOrder == 1) {
                        bow.setIsCurrentBowler(true);
                    }
                    bowlingRepository.save(bow);
                    bowOrder++;
                    if (bowOrder > 5) break;
                }
            }
        }
    }

    private void assignPlayerOfTheMatch(Match match) {
        // Find top performing player from match
        List<MatchInnings> inningsList = inningsRepository.findByMatchOrderByInningsNumberAsc(match);
        BattingScorecard topBatter = null;
        for (MatchInnings inn : inningsList) {
            List<BattingScorecard> batList = battingRepository.findByInningsOrderByBattingOrderAsc(inn);
            for (BattingScorecard b : batList) {
                if (topBatter == null || b.getRuns() > topBatter.getRuns()) {
                    topBatter = b;
                }
            }
        }

        if (topBatter != null && topBatter.getRuns() > 30) {
            match.setPlayerOfTheMatch(topBatter.getPlayer().getName() + " (" + topBatter.getRuns() + " runs off " + topBatter.getBalls() + " balls)");
        } else {
            match.setPlayerOfTheMatch("Star Player of the Match");
        }
    }

    private Double calculateOversDecimal(int legalBalls) {
        int overs = legalBalls / 6;
        int balls = legalBalls % 6;
        return Double.parseDouble(overs + "." + balls);
    }

    private String generateCommentary(String batsman, String bowler, int runs, boolean isWicket, String wicketType, boolean isExtra, String extraType) {
        if (isWicket) {
            String[] wicketPhrases = {
                    "OUT! Clean bowled! " + bowler + " produces an unplayable delivery shattering the stumps! " + batsman + " has to walk back!",
                    "GONE! In the air and taken! " + batsman + " mis-times the pull shot and holes out to the deep! Huge breakthrough by " + bowler + "!",
                    "TRAPPED IN FRONT! Loud appeal and given! " + bowler + " hits the back pad, plumb LBW! " + batsman + " is out!",
                    "EDGED AND CAUGHT! Lovely shape away from the right-hander, feather edge through to the keeper! " + bowler + " strikes!"
            };
            return wicketPhrases[random.nextInt(wicketPhrases.length)];
        }

        if (isExtra) {
            if ("WIDE".equalsIgnoreCase(extraType)) {
                return "Wide ball called! " + bowler + " slips this well outside off-stump, past the tramline. Free run conceded.";
            } else if ("NO_BALL".equalsIgnoreCase(extraType)) {
                return "NO-BALL! " + bowler + " oversteps the popping crease. Free hit signaled by the umpire!";
            }
            return "Extra conceded off this delivery, runs added to the tally.";
        }

        switch (runs) {
            case 6:
                String[] sixPhrases = {
                        "MASSIVE SIX! " + batsman + " climbs into the short ball and launches it into the top tier! What a colossal strike!",
                        "OUT OF THE PARK! " + batsman + " dances down the pitch and lofts " + bowler + " straight back over long-off for a 95-meter maximum!",
                        "DISPATCHED! Stand and deliver! " + batsman + " bludgeons it high and handsome over deep mid-wicket for SIX!"
                };
                return sixPhrases[random.nextInt(sixPhrases.length)];
            case 4:
                String[] fourPhrases = {
                        "FOUR! Exquisite cover drive from " + batsman + "! Pierces the infield with precision and races away to the fence!",
                        "CRACKING SHOT! Short and wide, " + batsman + " cuts it ferociously past point for four!",
                        "GLORIOUS! Leans into the half-volley and strokes it through mid-on with majestic timing for FOUR!"
                };
                return fourPhrases[random.nextInt(fourPhrases.length)];
            case 1:
                return "Pushed gently with soft hands into the gap on the on-side for a brisk single.";
            case 2:
                return "Good running! " + batsman + " works it into the pocket at deep square leg and hurries back for a sharp two.";
            case 3:
                return "Superb placement! Swept past the fine leg fielder, long chase to the boundary rope, three runs collected.";
            default:
                String[] dotPhrases = {
                        "Good length delivery on off stump, solidly defended on the front foot. Dot ball.",
                        "Beaten! Sharp seam movement off the deck whistles past the outside edge. Excellent ball from " + bowler + ".",
                        "Fuller length on middle, " + batsman + " squeezes it out straight to cover. No run."
                };
                return dotPhrases[random.nextInt(dotPhrases.length)];
        }
    }
}
