package com.cricpulse.cricket.config;

import com.cricpulse.cricket.model.*;
import com.cricpulse.cricket.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final MatchInningsRepository inningsRepository;
    private final BattingScorecardRepository battingRepository;
    private final BowlingScorecardRepository bowlingRepository;
    private final BallEventRepository ballEventRepository;
    private final FallOfWicketRepository fowRepository;

    public DataInitializer(TeamRepository teamRepository,
                           PlayerRepository playerRepository,
                           MatchRepository matchRepository,
                           MatchInningsRepository inningsRepository,
                           BattingScorecardRepository battingRepository,
                           BowlingScorecardRepository bowlingRepository,
                           BallEventRepository ballEventRepository,
                           FallOfWicketRepository fowRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.battingRepository = battingRepository;
        this.bowlingRepository = bowlingRepository;
        this.ballEventRepository = ballEventRepository;
        this.fowRepository = fowRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (teamRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data injection.");
            return;
        }

        log.info("Seeding CricPulse Cricket Score Management System...");

        // 1. Teams
        Team ind = teamRepository.save(new Team("India", "IND", "#1e3a8a", "#38bdf8", "🇮🇳"));
        Team aus = teamRepository.save(new Team("Australia", "AUS", "#b45309", "#facc15", "🇦🇺"));
        Team eng = teamRepository.save(new Team("England", "ENG", "#b91c1c", "#f87171", "🏴󠁧󠁢󠁥󠁮󠁧󠁿"));
        Team sa = teamRepository.save(new Team("South Africa", "SA", "#047857", "#34d399", "🇿🇦"));

        // 2. Players - Team India
        List<Player> indPlayers = new ArrayList<>();
        indPlayers.add(createPlayer("Rohit Sharma", ind, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm offbreak", 45, true, false, 158, 4231, 121, 32.5, 140.8, 5, 32, 0, 0.0, 0.0, "0/0"));
        indPlayers.add(createPlayer("Shubman Gill", ind, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm offbreak", 77, false, false, 28, 790, 126, 31.6, 142.1, 1, 4, 0, 0.0, 0.0, "0/0"));
        indPlayers.add(createPlayer("Virat Kohli", ind, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm medium", 18, false, false, 125, 4188, 122, 48.7, 137.9, 1, 38, 4, 20.0, 8.1, "1/13"));
        indPlayers.add(createPlayer("Suryakumar Yadav", ind, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm medium", 63, false, false, 72, 2340, 117, 43.3, 168.5, 4, 20, 0, 0.0, 0.0, "0/0"));
        indPlayers.add(createPlayer("Rishabh Pant", ind, PlayerRole.WICKET_KEEPER, "Left-hand bat", "None", 17, false, true, 76, 1209, 65, 23.2, 127.3, 0, 3, 0, 0.0, 0.0, "0/0"));
        indPlayers.add(createPlayer("Hardik Pandya", ind, PlayerRole.ALL_ROUNDER, "Right-hand bat", "Right-arm fast-medium", 33, false, false, 102, 1580, 71, 26.8, 139.7, 0, 4, 84, 25.4, 8.15, "4/16"));
        indPlayers.add(createPlayer("Ravindra Jadeja", ind, PlayerRole.ALL_ROUNDER, "Left-hand bat", "Slow left-arm orthodox", 8, false, false, 74, 515, 46, 21.4, 125.6, 0, 0, 54, 28.5, 7.10, "3/15"));
        indPlayers.add(createPlayer("Axar Patel", ind, PlayerRole.ALL_ROUNDER, "Left-hand bat", "Slow left-arm orthodox", 20, false, false, 60, 450, 65, 19.5, 142.3, 0, 1, 62, 22.1, 7.30, "3/9"));
        indPlayers.add(createPlayer("Kuldeep Yadav", ind, PlayerRole.BOWLER, "Left-hand bat", "Left-arm wrist spin", 23, false, false, 40, 65, 23, 10.8, 88.0, 0, 0, 69, 14.1, 6.74, "5/17"));
        indPlayers.add(createPlayer("Jasprit Bumrah", ind, PlayerRole.BOWLER, "Right-hand bat", "Right-arm fast", 93, false, false, 70, 38, 16, 7.6, 68.0, 0, 0, 89, 17.7, 6.27, "4/18"));
        indPlayers.add(createPlayer("Mohammed Siraj", ind, PlayerRole.BOWLER, "Right-hand bat", "Right-arm fast", 73, false, false, 42, 22, 10, 5.5, 75.0, 0, 0, 46, 26.2, 7.85, "4/17"));
        indPlayers = playerRepository.saveAll(indPlayers);

        // 3. Players - Team Australia
        List<Player> ausPlayers = new ArrayList<>();
        ausPlayers.add(createPlayer("Travis Head", aus, PlayerRole.BATSMAN, "Left-hand bat", "Right-arm offbreak", 62, false, false, 40, 1098, 91, 31.3, 154.2, 0, 7, 3, 35.0, 8.4, "1/10"));
        ausPlayers.add(createPlayer("David Warner", aus, PlayerRole.BATSMAN, "Left-hand bat", "Right-arm legbreak", 31, false, false, 110, 3277, 100, 33.4, 142.5, 1, 26, 0, 0.0, 0.0, "0/0"));
        ausPlayers.add(createPlayer("Mitchell Marsh", aus, PlayerRole.ALL_ROUNDER, "Right-hand bat", "Right-arm medium", 8, true, false, 62, 1510, 92, 33.5, 134.8, 0, 9, 17, 24.1, 8.2, "3/24"));
        ausPlayers.add(createPlayer("Glenn Maxwell", aus, PlayerRole.ALL_ROUNDER, "Right-hand bat", "Right-arm offbreak", 32, false, false, 106, 2600, 145, 29.5, 155.4, 5, 11, 43, 27.2, 7.5, "3/10"));
        ausPlayers.add(createPlayer("Marcus Stoinis", aus, PlayerRole.ALL_ROUNDER, "Right-hand bat", "Right-arm fast-medium", 17, false, false, 68, 1140, 78, 30.0, 144.5, 0, 5, 41, 23.8, 8.35, "3/18"));
        ausPlayers.add(createPlayer("Josh Inglis", aus, PlayerRole.WICKET_KEEPER, "Right-hand bat", "None", 48, false, true, 26, 550, 110, 27.5, 151.0, 1, 1, 0, 0.0, 0.0, "0/0"));
        ausPlayers.add(createPlayer("Tim David", aus, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm offbreak", 85, false, false, 45, 1080, 92, 36.0, 162.4, 0, 6, 5, 22.0, 8.9, "1/15"));
        ausPlayers.add(createPlayer("Pat Cummins", aus, PlayerRole.BOWLER, "Right-hand bat", "Right-arm fast", 30, false, false, 55, 185, 28, 12.3, 120.0, 0, 0, 66, 23.1, 7.35, "3/15"));
        ausPlayers.add(createPlayer("Mitchell Starc", aus, PlayerRole.BOWLER, "Left-hand bat", "Left-arm fast", 56, false, false, 65, 95, 14, 9.5, 90.0, 0, 0, 79, 23.8, 7.74, "4/20"));
        ausPlayers.add(createPlayer("Adam Zampa", aus, PlayerRole.BOWLER, "Right-hand bat", "Right-arm legbreak", 88, false, false, 88, 55, 13, 6.8, 80.0, 0, 0, 111, 21.8, 7.20, "5/19"));
        ausPlayers.add(createPlayer("Josh Hazlewood", aus, PlayerRole.BOWLER, "Left-hand bat", "Right-arm fast-medium", 38, false, false, 50, 20, 7, 5.0, 60.0, 0, 0, 64, 21.2, 7.15, "4/12"));
        ausPlayers = playerRepository.saveAll(ausPlayers);

        // 4. Players - Team England
        List<Player> engPlayers = new ArrayList<>();
        engPlayers.add(createPlayer("Jos Buttler", eng, PlayerRole.WICKET_KEEPER, "Right-hand bat", "None", 63, true, true, 124, 3264, 101, 35.8, 146.3, 1, 24, 0, 0.0, 0.0, "0/0"));
        engPlayers.add(createPlayer("Phil Salt", eng, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm offbreak", 28, false, false, 38, 1020, 119, 34.0, 165.8, 2, 4, 0, 0.0, 0.0, "0/0"));
        engPlayers.add(createPlayer("Will Jacks", eng, PlayerRole.ALL_ROUNDER, "Right-hand bat", "Right-arm offbreak", 85, false, false, 20, 410, 52, 25.6, 150.0, 0, 2, 8, 24.5, 7.9, "2/18"));
        engPlayers.add(createPlayer("Harry Brook", eng, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm medium", 88, false, false, 39, 740, 81, 29.6, 145.0, 0, 3, 0, 0.0, 0.0, "0/0"));
        engPlayers.add(createPlayer("Liam Livingstone", eng, PlayerRole.ALL_ROUNDER, "Right-hand bat", "Right-arm legbreak", 23, false, false, 48, 780, 103, 24.3, 149.2, 1, 2, 23, 25.1, 8.2, "3/17"));
        engPlayers.add(createPlayer("Sam Curran", eng, PlayerRole.ALL_ROUNDER, "Left-hand bat", "Left-arm medium-fast", 58, false, false, 53, 310, 34, 14.7, 125.0, 0, 0, 52, 24.8, 8.05, "5/10"));
        engPlayers.add(createPlayer("Moeen Ali", eng, PlayerRole.ALL_ROUNDER, "Left-hand bat", "Right-arm offbreak", 18, false, false, 92, 1229, 72, 21.9, 142.4, 0, 7, 51, 27.6, 8.22, "3/24"));
        engPlayers.add(createPlayer("Chris Jordan", eng, PlayerRole.BOWLER, "Right-hand bat", "Right-arm fast-medium", 34, false, false, 95, 450, 36, 14.0, 131.0, 0, 0, 108, 27.4, 8.72, "4/6"));
        engPlayers.add(createPlayer("Jofra Archer", eng, PlayerRole.BOWLER, "Right-hand bat", "Right-arm fast", 22, false, false, 25, 60, 16, 10.0, 115.0, 0, 0, 33, 22.0, 7.30, "3/16"));
        engPlayers.add(createPlayer("Adil Rashid", eng, PlayerRole.BOWLER, "Right-hand bat", "Right-arm legbreak", 95, false, false, 114, 115, 22, 6.7, 85.0, 0, 0, 120, 24.3, 7.38, "4/2"));
        engPlayers.add(createPlayer("Reece Topley", eng, PlayerRole.BOWLER, "Right-hand bat", "Left-arm fast-medium", 83, false, false, 32, 15, 6, 5.0, 60.0, 0, 0, 35, 25.1, 7.95, "3/22"));
        engPlayers = playerRepository.saveAll(engPlayers);

        // 5. Players - Team South Africa
        List<Player> saPlayers = new ArrayList<>();
        saPlayers.add(createPlayer("Quinton de Kock", sa, PlayerRole.WICKET_KEEPER, "Left-hand bat", "None", 12, false, true, 92, 2584, 100, 31.5, 138.3, 1, 16, 0, 0.0, 0.0, "0/0"));
        saPlayers.add(createPlayer("Reeza Hendricks", sa, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm offbreak", 17, false, false, 68, 1950, 83, 31.4, 131.0, 0, 16, 0, 0.0, 0.0, "0/0"));
        saPlayers.add(createPlayer("Aiden Markram", sa, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm offbreak", 4, true, false, 51, 1250, 70, 33.7, 146.8, 0, 9, 12, 25.0, 7.6, "3/21"));
        saPlayers.add(createPlayer("Heinrich Klaasen", sa, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm offbreak", 45, false, false, 52, 980, 81, 24.5, 147.2, 0, 5, 0, 0.0, 0.0, "0/0"));
        saPlayers.add(createPlayer("David Miller", sa, PlayerRole.BATSMAN, "Left-hand bat", "Right-arm offbreak", 10, false, false, 126, 2437, 106, 33.8, 140.5, 2, 7, 0, 0.0, 0.0, "0/0"));
        saPlayers.add(createPlayer("Tristan Stubbs", sa, PlayerRole.BATSMAN, "Right-hand bat", "Right-arm offbreak", 30, false, false, 28, 480, 72, 24.0, 145.0, 0, 2, 2, 30.0, 8.5, "1/12"));
        saPlayers.add(createPlayer("Marco Jansen", sa, PlayerRole.ALL_ROUNDER, "Right-hand bat", "Left-arm fast", 70, false, false, 21, 120, 25, 15.0, 125.0, 0, 0, 22, 29.0, 8.4, "3/52"));
        saPlayers.add(createPlayer("Keshav Maharaj", sa, PlayerRole.BOWLER, "Right-hand bat", "Slow left-arm orthodox", 16, false, false, 37, 85, 15, 9.4, 90.0, 0, 0, 43, 23.5, 7.15, "3/23"));
        saPlayers.add(createPlayer("Kagiso Rabada", sa, PlayerRole.BOWLER, "Left-hand bat", "Right-arm fast", 25, false, false, 65, 140, 22, 10.0, 98.0, 0, 0, 71, 27.2, 8.45, "3/20"));
        saPlayers.add(createPlayer("Anrich Nortje", sa, PlayerRole.BOWLER, "Right-hand bat", "Right-arm fast", 20, false, false, 40, 25, 7, 5.0, 70.0, 0, 0, 53, 19.8, 7.10, "4/10"));
        saPlayers.add(createPlayer("Tabraiz Shamsi", sa, PlayerRole.BOWLER, "Right-hand bat", "Left-arm wrist spin", 26, false, false, 70, 15, 4, 3.7, 55.0, 0, 0, 89, 22.1, 7.39, "5/24"));
        saPlayers = playerRepository.saveAll(saPlayers);

        // 6. Seed Matches
        seedMatch1Live(ind, aus, indPlayers, ausPlayers);
        seedMatch2Upcoming(eng, sa);
        seedMatch3Completed(ind, eng, indPlayers, engPlayers);

        log.info("CricPulse Database Initialized successfully!");
    }

    private Player createPlayer(String name, Team team, PlayerRole role, String battingStyle, String bowlingStyle,
                                 int jersey, boolean isCaptain, boolean isKeeper, int matches, int runs, int hs,
                                 double avg, double sr, int hundreds, int fifties, int wickets, double bowlAvg,
                                 double econ, String bb) {
        Player p = new Player(name, team, role, battingStyle, bowlingStyle, jersey);
        p.setIsCaptain(isCaptain);
        p.setIsWicketKeeper(isKeeper);
        p.setMatchesPlayed(matches);
        p.setRunsScored(runs);
        p.setHighestScore(hs);
        p.setBattingAverage(avg);
        p.setBattingStrikeRate(sr);
        p.setCenturies(hundreds);
        p.setFifties(fifties);
        p.setWicketsTaken(wickets);
        p.setBowlingAverage(bowlAvg);
        p.setBowlingEconomy(econ);
        p.setBestBowling(bb);
        return p;
    }

    private void seedMatch1Live(Team ind, Team aus, List<Player> indPlayers, List<Player> ausPlayers) {
        Match match = new Match();
        match.setMatchTitle("ICC Men's T20 World Cup - Semi Final 1");
        match.setSeriesName("ICC Men's T20 World Cup 2026");
        match.setMatchType("T20");
        match.setVenue("Wankhede Stadium, Mumbai");
        match.setStatus(MatchStatus.LIVE);
        match.setTeamA(ind);
        match.setTeamB(aus);
        match.setTossWinner("IND");
        match.setTossDecision("BAT");
        match.setTotalOvers(20);
        match.setCurrentInnings(2);
        match.setTarget(193);
        match.setIsSimulationActive(false);
        match.setSimulationSpeedMs(2000);
        match = matchRepository.save(match);

        // Innings 1: India 192/5 (20.0 ov)
        MatchInnings inn1 = new MatchInnings();
        inn1.setMatch(match);
        inn1.setInningsNumber(1);
        inn1.setBattingTeam(ind);
        inn1.setBowlingTeam(aus);
        inn1.setTotalRuns(192);
        inn1.setTotalWickets(5);
        inn1.setLegalBalls(120);
        inn1.setOversBowled(20.0);
        inn1.setExtras(10);
        inn1.setWides(5);
        inn1.setNoBalls(1);
        inn1.setByes(2);
        inn1.setLegByes(2);
        inn1.setCurrentRunRate(9.60);
        inn1.setIsCompleted(true);
        inn1 = inningsRepository.save(inn1);

        // Innings 1 Batting
        createBatting(inn1, indPlayers.get(0), 1, 48, 28, 5, 2, 171.4, true, "c Warner b Starc");
        createBatting(inn1, indPlayers.get(1), 2, 22, 16, 3, 0, 137.5, true, "b Cummins");
        createBatting(inn1, indPlayers.get(2), 3, 74, 46, 7, 3, 160.8, true, "c Maxwell b Zampa");
        createBatting(inn1, indPlayers.get(3), 4, 18, 11, 2, 1, 163.6, true, "c Marsh b Stoinis");
        createBatting(inn1, indPlayers.get(4), 5, 6, 5, 1, 0, 120.0, true, "run out (Head)");
        createBatting(inn1, indPlayers.get(5), 6, 21, 12, 1, 2, 175.0, false, "not out");
        createBatting(inn1, indPlayers.get(6), 7, 3, 2, 0, 0, 150.0, false, "not out");

        // Innings 1 Bowling
        createBowling(inn1, ausPlayers.get(8), 1, 24, 0, 42, 1, 10.5, 8); // Starc
        createBowling(inn1, ausPlayers.get(10), 2, 24, 0, 32, 0, 8.0, 11); // Hazlewood
        createBowling(inn1, ausPlayers.get(7), 3, 24, 0, 38, 1, 9.5, 9); // Cummins
        createBowling(inn1, ausPlayers.get(9), 4, 24, 0, 36, 1, 9.0, 7); // Zampa
        createBowling(inn1, ausPlayers.get(4), 5, 24, 0, 44, 1, 11.0, 5); // Stoinis

        // Innings 1 Fall of Wickets
        fowRepository.save(new FallOfWicket(inn1, 1, indPlayers.get(1), 38, "4.2"));
        fowRepository.save(new FallOfWicket(inn1, 2, indPlayers.get(0), 92, "9.5"));
        fowRepository.save(new FallOfWicket(inn1, 3, indPlayers.get(3), 126, "13.4"));
        fowRepository.save(new FallOfWicket(inn1, 4, indPlayers.get(4), 145, "15.3"));
        fowRepository.save(new FallOfWicket(inn1, 5, indPlayers.get(2), 184, "19.1"));

        // Innings 2: Australia chasing 193 - currently 154/3 in 16.2 overs
        MatchInnings inn2 = new MatchInnings();
        inn2.setMatch(match);
        inn2.setInningsNumber(2);
        inn2.setBattingTeam(aus);
        inn2.setBowlingTeam(ind);
        inn2.setTotalRuns(154);
        inn2.setTotalWickets(3);
        inn2.setLegalBalls(98); // 16.2 overs
        inn2.setOversBowled(16.2);
        inn2.setExtras(7);
        inn2.setWides(4);
        inn2.setNoBalls(1);
        inn2.setByes(0);
        inn2.setLegByes(2);
        inn2.setCurrentRunRate(9.42);
        inn2.setRequiredRunRate(10.63); // 39 runs needed in 22 balls
        inn2.setIsCompleted(false);
        inn2 = inningsRepository.save(inn2);

        // Innings 2 Batting
        createBatting(inn2, ausPlayers.get(0), 1, 62, 38, 7, 3, 163.1, true, "c Pant b Bumrah");
        createBatting(inn2, ausPlayers.get(1), 2, 18, 14, 2, 0, 128.5, true, "b Siraj");
        createBatting(inn2, ausPlayers.get(2), 3, 31, 20, 3, 1, 155.0, true, "c Kohli b Kuldeep");
        BattingScorecard maxwell = createBatting(inn2, ausPlayers.get(3), 4, 32, 18, 2, 2, 177.7, false, "not out");
        maxwell.setIsOnCrease(true);
        maxwell.setIsStriker(true);
        battingRepository.save(maxwell);

        BattingScorecard stoinis = createBatting(inn2, ausPlayers.get(4), 5, 8, 6, 1, 0, 133.3, false, "not out");
        stoinis.setIsOnCrease(true);
        stoinis.setIsStriker(false);
        battingRepository.save(stoinis);

        for (int i = 5; i < ausPlayers.size(); i++) {
            createBatting(inn2, ausPlayers.get(i), i + 1, 0, 0, 0, 0, 0.0, false, "yet to bat");
        }

        // Innings 2 Bowling
        createBowling(inn2, indPlayers.get(9), 1, 20, 0, 22, 1, 6.60, 11); // Bumrah (3.2 ov - current bowler)
        BowlingScorecard bumrah = bowlingRepository.findByInningsAndPlayer(inn2, indPlayers.get(9)).get();
        bumrah.setIsCurrentBowler(true);
        bowlingRepository.save(bumrah);

        createBowling(inn2, indPlayers.get(10), 2, 24, 0, 41, 1, 10.25, 7); // Siraj
        createBowling(inn2, indPlayers.get(5), 3, 18, 0, 29, 0, 9.66, 6); // Hardik
        createBowling(inn2, indPlayers.get(8), 4, 24, 0, 38, 1, 9.50, 8); // Kuldeep
        createBowling(inn2, indPlayers.get(6), 5, 12, 0, 22, 0, 11.00, 3); // Jadeja

        // Innings 2 Fall of Wickets
        fowRepository.save(new FallOfWicket(inn2, 1, ausPlayers.get(1), 32, "3.4"));
        fowRepository.save(new FallOfWicket(inn2, 2, ausPlayers.get(0), 108, "11.2"));
        fowRepository.save(new FallOfWicket(inn2, 3, ausPlayers.get(2), 142, "14.5"));

        // Seed recent Ball Events for over 16 (16.1, 16.2)
        saveBallEvent(match, inn2, 16, 1, "16.1", maxwell.getPlayer(), stoinis.getPlayer(), bumrah.getPlayer(), 1, false, null, null, false, null, 0, 1,
                "Bumrah bowls a searing yorker on off stump, Maxwell jams his bat down and scrambles through for a single.", "153/3");
        saveBallEvent(match, inn2, 16, 2, "16.2", stoinis.getPlayer(), maxwell.getPlayer(), bumrah.getPlayer(), 1, false, null, null, false, null, 0, 1,
                "Back of a length angling into the ribs, tucked away neatly behind square for one more.", "154/3");
    }

    private void seedMatch2Upcoming(Team eng, Team sa) {
        Match match = new Match();
        match.setMatchTitle("ICC Men's T20 World Cup - Semi Final 2");
        match.setSeriesName("ICC Men's T20 World Cup 2026");
        match.setMatchType("T20");
        match.setVenue("Eden Gardens, Kolkata");
        match.setStatus(MatchStatus.UPCOMING);
        match.setTeamA(eng);
        match.setTeamB(sa);
        match.setTotalOvers(20);
        match.setCurrentInnings(1);
        match.setScheduledAt(LocalDateTime.now().plusHours(2));
        matchRepository.save(match);
    }

    private void seedMatch3Completed(Team ind, Team eng, List<Player> indPlayers, List<Player> engPlayers) {
        Match match = new Match();
        match.setMatchTitle("ICC Men's T20 World Cup - Super 8 Group 1");
        match.setSeriesName("ICC Men's T20 World Cup 2026");
        match.setMatchType("T20");
        match.setVenue("M. Chinnaswamy Stadium, Bengaluru");
        match.setStatus(MatchStatus.COMPLETED);
        match.setTeamA(ind);
        match.setTeamB(eng);
        match.setTossWinner("ENG");
        match.setTossDecision("BOWL");
        match.setTotalOvers(20);
        match.setCurrentInnings(2);
        match.setTarget(199);
        match.setResultSummary("India won by 14 runs!");
        match.setPlayerOfTheMatch("Jasprit Bumrah (4/18 in 4.0 ov)");
        match = matchRepository.save(match);

        // Innings 1: India 198/6
        MatchInnings inn1 = new MatchInnings();
        inn1.setMatch(match);
        inn1.setInningsNumber(1);
        inn1.setBattingTeam(ind);
        inn1.setBowlingTeam(eng);
        inn1.setTotalRuns(198);
        inn1.setTotalWickets(6);
        inn1.setLegalBalls(120);
        inn1.setOversBowled(20.0);
        inn1.setCurrentRunRate(9.90);
        inn1.setIsCompleted(true);
        inn1 = inningsRepository.save(inn1);

        createBatting(inn1, indPlayers.get(0), 1, 57, 34, 6, 3, 167.6, true, "c Brook b Archer");
        createBatting(inn1, indPlayers.get(2), 2, 68, 44, 7, 2, 154.5, true, "c Buttler b Curran");
        createBatting(inn1, indPlayers.get(3), 3, 44, 21, 4, 3, 209.5, true, "b Rashid");

        // Innings 2: England 184/8
        MatchInnings inn2 = new MatchInnings();
        inn2.setMatch(match);
        inn2.setInningsNumber(2);
        inn2.setBattingTeam(eng);
        inn2.setBowlingTeam(ind);
        inn2.setTotalRuns(184);
        inn2.setTotalWickets(8);
        inn2.setLegalBalls(120);
        inn2.setOversBowled(20.0);
        inn2.setCurrentRunRate(9.20);
        inn2.setIsCompleted(true);
        inn2 = inningsRepository.save(inn2);

        createBatting(inn2, engPlayers.get(0), 1, 52, 32, 5, 2, 162.5, true, "b Bumrah");
        createBatting(inn2, engPlayers.get(1), 2, 38, 22, 4, 2, 172.7, true, "c Rohit b Siraj");
        createBatting(inn2, engPlayers.get(3), 3, 41, 26, 3, 2, 157.6, true, "c Jadeja b Bumrah");

        createBowling(inn2, indPlayers.get(9), 1, 24, 1, 18, 4, 4.50, 14); // Bumrah 4/18
    }

    private BattingScorecard createBatting(MatchInnings inn, Player player, int order, int runs, int balls, int fours,
                                           int sixes, double sr, boolean isOut, String dismissal) {
        BattingScorecard b = new BattingScorecard();
        b.setInnings(inn);
        b.setPlayer(player);
        b.setBattingOrder(order);
        b.setRuns(runs);
        b.setBalls(balls);
        b.setFours(fours);
        b.setSixes(sixes);
        b.setStrikeRate(sr);
        b.setIsOut(isOut);
        b.setDismissal(dismissal);
        return battingRepository.save(b);
    }

    private BowlingScorecard createBowling(MatchInnings inn, Player player, int order, int legalBalls, int maidens,
                                           int runs, int wickets, double econ, int dots) {
        BowlingScorecard bow = new BowlingScorecard();
        bow.setInnings(inn);
        bow.setPlayer(player);
        bow.setBowlingOrder(order);
        bow.setLegalBalls(legalBalls);
        bow.setOvers(Double.parseDouble((legalBalls / 6) + "." + (legalBalls % 6)));
        bow.setMaidens(maidens);
        bow.setRunsConceded(runs);
        bow.setWickets(wickets);
        bow.setEconomy(econ);
        bow.setDots(dots);
        return bowlingRepository.save(bow);
    }

    private void saveBallEvent(Match match, MatchInnings inn, int over, int ballInOver, String ballDisplay,
                               Player bat, Player nonStr, Player bow, int runs, boolean isWicket, String wType,
                               Player outP, boolean isExtra, String extraType, int extraRuns, int totalRuns,
                               String commentary, String scoreAfter) {
        BallEvent be = new BallEvent();
        be.setMatch(match);
        be.setInnings(inn);
        be.setOverNumber(over);
        be.setBallNumberInOver(ballInOver);
        be.setBallDisplay(ballDisplay);
        be.setBatsman(bat);
        be.setNonStriker(nonStr);
        be.setBowler(bow);
        be.setRunsScored(runs);
        be.setIsWicket(isWicket);
        be.setWicketType(wType);
        be.setOutPlayer(outP);
        be.setIsExtra(isExtra);
        be.setExtraType(extraType);
        be.setExtraRuns(extraRuns);
        be.setTotalBallRuns(totalRuns);
        be.setCommentary(commentary);
        be.setScoreAfterBall(scoreAfter);
        be.setTimestamp(LocalDateTime.now().minusSeconds(10));
        ballEventRepository.save(be);
    }
}
