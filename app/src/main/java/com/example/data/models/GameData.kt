package com.example.data.models

object GameData {

    val TRUCK_MODELS = listOf(
        TruckModelConfig(
            id = "tata_1613",
            name = "Tata LPT 1613 \"Himalayan Chinar\"",
            badge = "TATA 1613 SE",
            price = 0L,
            enginePowerHp = 135,
            topSpeedKmh = 130,
            weightTons = 5.2f,
            olengResponsiveness = 1.0f,
            description = "The undisputed king of Kashmir & Ladakh mountain highways! Carved wooden body, high mountain climbing torque, agile turning, and signature exhaust whistle."
        ),
        TruckModelConfig(
            id = "eicher_3019",
            name = "Eicher Pro 3019 \"Kashmir Apple Express\"",
            badge = "EICHER 3019",
            price = 350000L,
            enginePowerHp = 190,
            topSpeedKmh = 125,
            weightTons = 7.8f,
            olengResponsiveness = 0.88f,
            description = "High-speed intercity fruit carrier. Engineered for rapid transit of fresh Kashmiri apples and valley harvest down through NH-44 and Banihal passes."
        ),
        TruckModelConfig(
            id = "leyland_2820",
            name = "Ashok Leyland 2820 \"Jhelum Cruiser\" (10-Wheeler)",
            badge = "LEYLAND 2820",
            price = 750000L,
            enginePowerHp = 260,
            topSpeedKmh = 112,
            weightTons = 12.8f,
            olengResponsiveness = 0.72f,
            description = "Heavy multi-axle 10-wheel heavy hauler built for extreme freight across high mountain passes, timber transport, and heavy valley infrastructure."
        ),
        TruckModelConfig(
            id = "mahindra_blazo",
            name = "Mahindra Blazo X 280 \"Pir Panjal Titan\"",
            badge = "BLAZO X 280",
            price = 520000L,
            enginePowerHp = 280,
            topSpeedKmh = 120,
            weightTons = 9.5f,
            olengResponsiveness = 0.82f,
            description = "High-torque modern mountain hauler engineered to conquer treacherous steep inclines on Mughal Road and Zojila Pass without stalling."
        )
    )

    val LIVERY_OPTIONS = listOf(
        LiveryOption(
            id = "chinar_express",
            name = "Chinar Express 786",
            subtitle = "Kashmir to Kanyakumari Special",
            primaryColorHex = 0xFFEAB308L, // Saffron Gold
            secondaryColorHex = 0xFFDC2626L, // Crimson
            accentColorHex = 0xFFFFFFFFL,
            tarpaulinColorHex = 0xFF1E3A8AL, // Deep Blue
            quoteText = "HORN PLEASE - DILBAR JANI",
            mudflapText = "JANNAT-E-KASHMIR",
            sideText = "CHINAR EXPRESS - SRINAGAR",
            windshieldBanner = "SHER-E-KASHMIR",
            price = 0L,
            requiredLevel = 1
        ),
        LiveryOption(
            id = "pari_mahal",
            name = "Pari Mahal Royal",
            subtitle = "Dal Lake Highway Cruiser",
            primaryColorHex = 0xFF2563EBL, // Lake Blue
            secondaryColorHex = 0xFFFAC815L, // Gold
            accentColorHex = 0xFFFFFFFFL,
            tarpaulinColorHex = 0xFFDC2626L,
            quoteText = "USE DIPPER AT NIGHT",
            mudflapText = "DEKHO MAGAR PYAAR SE",
            sideText = "PARI MAHAL - VALLEY RUNNER",
            windshieldBanner = "KASHMIR FALCON",
            price = 45000L,
            requiredLevel = 2
        ),
        LiveryOption(
            id = "pir_panjal",
            name = "Pir Panjal Warrior",
            subtitle = "Mughal Road Heavy Freight",
            primaryColorHex = 0xFFFFFFFFL,
            secondaryColorHex = 0xFFEA580CL, // Saffron Orange
            accentColorHex = 0xFF16A34AL,
            tarpaulinColorHex = 0xFFEA580CL,
            quoteText = "HAS MAT PAGLI PYAAR HO JAYEGA",
            mudflapText = "PIR PANJAL SQUAD",
            sideText = "MUGHAL ROAD EXPEDITION",
            windshieldBanner = "PEER KI GALI MASTER",
            price = 75000L,
            requiredLevel = 3
        ),
        LiveryOption(
            id = "pampore_zafran",
            name = "Pampore Saffron Gold",
            subtitle = "Midnight Valley Hauler",
            primaryColorHex = 0xFF18181BL, // Night Black
            secondaryColorHex = 0xFFE11D48L, // Kashmiri Red
            accentColorHex = 0xFFF59E0BL,
            tarpaulinColorHex = 0xFF0F172AL,
            quoteText = "DUM HAI TO CROSS KAR",
            mudflapText = "ZAFRAN PRIDE",
            sideText = "KASHMIR GOLD LOGISTICS",
            windshieldBanner = "VALLEY NIGHT RUNNER",
            price = 95000L,
            requiredLevel = 4
        ),
        LiveryOption(
            id = "gulmarg_snow",
            name = "Gulmarg Snow Rider",
            subtitle = "High Altitude Mountain Pass",
            primaryColorHex = 0xFF9333EAL, // Royal Purple
            secondaryColorHex = 0xFFEC4899L, // Pink
            accentColorHex = 0xFF38BDF8L,
            tarpaulinColorHex = 0xFF581C87L,
            quoteText = "SPEED THRILLS BUT KILLS",
            mudflapText = "GULMARG EXPRESS",
            sideText = "SNOW VALLEY FREIGHT",
            windshieldBanner = "HIMALAYAN DRIFTER",
            price = 120000L,
            requiredLevel = 5
        ),
        LiveryOption(
            id = "shopian_apple",
            name = "Shopian Apple King 99",
            subtitle = "Srinagar - Delhi Fast Transit",
            primaryColorHex = 0xFF16A34AL, // Orchard Green
            secondaryColorHex = 0xFFEAB308L, // Apple Gold
            accentColorHex = 0xFFFFFFFFL,
            tarpaulinColorHex = 0xFF15803DL,
            quoteText = "FRESH KASHMIRI SEB RUSH",
            mudflapText = "APPLE KING 99",
            sideText = "SHOPIAN FRESH HARVEST",
            windshieldBanner = "FULL THROTTLE KASHMIR",
            price = 85000L,
            requiredLevel = 3
        )
    )

    val HORN_OPTIONS = listOf(
        HornOption(
            id = "basuri_v3",
            name = "Himalayan 5-Pipe Melody Horn",
            category = "Himalayan Horn",
            price = 0L,
            requiredLevel = 1,
            melodyNotes = listOf(
                HornNote(523.25f, 140),
                HornNote(659.25f, 140),
                HornNote(783.99f, 140),
                HornNote(880.00f, 220),
                HornNote(783.99f, 140),
                HornNote(659.25f, 140),
                HornNote(523.25f, 320)
            )
        ),
        HornOption(
            id = "kashmir_dilbar",
            name = "Kashmir Dilbar Snaking Tune",
            category = "Himalayan Horn",
            price = 35000L,
            requiredLevel = 2,
            melodyNotes = listOf(
                HornNote(440.00f, 90),
                HornNote(493.88f, 90),
                HornNote(554.37f, 90),
                HornNote(587.33f, 90),
                HornNote(659.25f, 90),
                HornNote(739.99f, 90),
                HornNote(880.00f, 240),
                HornNote(739.99f, 120),
                HornNote(880.00f, 340)
            )
        ),
        HornOption(
            id = "punjab_kashmir",
            name = "Punjab-Kashmir Highway Horn",
            category = "Himalayan Horn",
            price = 45000L,
            requiredLevel = 3,
            melodyNotes = listOf(
                HornNote(587.33f, 140),
                HornNote(659.25f, 140),
                HornNote(783.99f, 120),
                HornNote(783.99f, 120),
                HornNote(783.99f, 120),
                HornNote(783.99f, 120),
                HornNote(783.99f, 220)
            )
        ),
        HornOption(
            id = "rouf_chime",
            name = "Kashmir Valley Rouf Chime",
            category = "Himalayan Horn",
            price = 50000L,
            requiredLevel = 4,
            melodyNotes = listOf(
                HornNote(659.25f, 160),
                HornNote(587.33f, 130),
                HornNote(523.25f, 160),
                HornNote(493.88f, 130),
                HornNote(440.00f, 300),
                HornNote(523.25f, 180),
                HornNote(493.88f, 380)
            )
        ),
        HornOption(
            id = "dual_air",
            name = "Standard Heavy Dual Air Horn",
            category = "Dual Air Horn",
            price = 15000L,
            requiredLevel = 1,
            melodyNotes = listOf(
                HornNote(220.00f, 500),
                HornNote(277.18f, 500)
            )
        ),
        HornOption(
            id = "mountain_foghorn",
            name = "Deep Mountain Pass Foghorn",
            category = "Heavy Fog Horn",
            price = 60000L,
            requiredLevel = 3,
            melodyNotes = listOf(
                HornNote(98.00f, 800),
                HornNote(123.47f, 800),
                HornNote(146.83f, 800)
            )
        )
    )

    val ROUTES = listOf(
        RouteLocation(
            id = "nh44_highway",
            name = "NH-44 Jammu-Srinagar Highway (Banihal - Qazigund Corridor)",
            region = "Kashmir Valley",
            lengthKm = 18.5f,
            difficulty = "Moderate",
            inclineGradePct = 6,
            curvesDensity = 4,
            description = "The lifeline of Kashmir! High-speed national corridor carving through the Pir Panjal mountains, passing through pine forests, Jawahar Tunnel, roadside Kehwa dhabas, and Indian Oil petrol pumps."
        ),
        RouteLocation(
            id = "mughal_road",
            name = "Mughal Road & Peer Ki Gali Mountain Pass (3,490m)",
            region = "Pir Panjal Range",
            lengthKm = 14.2f,
            difficulty = "Extreme",
            inclineGradePct = 22,
            curvesDensity = 9,
            description = "Legendary historical mountain pass climbing to 3,490m elevation. Steep hairpin bends and narrow cliff curves demand heavy engine torque and skilled mountain steering!"
        ),
        RouteLocation(
            id = "zojila_pass",
            name = "Zojila Pass Gateway (Sonamarg to Dras Corridor)",
            region = "Greater Himalayas",
            lengthKm = 16.0f,
            difficulty = "Winding",
            inclineGradePct = 14,
            curvesDensity = 8,
            description = "One of the most adventurous roads in the world, carved into towering granite cliffs with breathtaking mountain gorge vistas and viaduct bridges."
        ),
        RouteLocation(
            id = "gulmarg_pass",
            name = "Srinagar - Tangmarg - Gulmarg Pine Corridor",
            region = "Baramulla District",
            lengthKm = 15.5f,
            difficulty = "Moderate",
            inclineGradePct = 12,
            curvesDensity = 7,
            description = "Enchanting mountain climb weaving through dense cedar and pine forests, fragrant apple orchards, and misty mountain village switchbacks."
        )
    )

    val CARGO_JOBS = listOf(
        CargoJob(
            id = "job_apple_crates",
            cargoName = "Fresh Kashmiri Golden Apples (Shopian Express)",
            category = "Agriculture",
            weightTons = 4.8f,
            originCity = "Shopian Orchards",
            destinationCity = "Srinagar Fruit Mandi",
            routeId = "nh44_highway",
            baseRewardInr = 38500L,
            fragility = 0.85f,
            timeLimitSec = 240
        ),
        CargoJob(
            id = "job_pampore_saffron",
            cargoName = "Pure Saffron & Valley Honey (Pampore Zafran)",
            category = "Agriculture",
            weightTons = 2.2f,
            originCity = "Pampore",
            destinationCity = "Srinagar Lal Chowk",
            routeId = "nh44_highway",
            baseRewardInr = 46000L,
            fragility = 0.90f,
            timeLimitSec = 210
        ),
        CargoJob(
            id = "job_walnut_dryfruit",
            cargoName = "Kashmiri Walnuts & Dried Almonds (Dry Fruit Sacks)",
            category = "Agriculture",
            weightTons = 6.4f,
            originCity = "Baramulla",
            destinationCity = "Banihal Transit Depot",
            routeId = "zojila_pass",
            baseRewardInr = 42500L,
            fragility = 0.65f,
            timeLimitSec = 280
        ),
        CargoJob(
            id = "job_kashmir_willow",
            cargoName = "Sangam English & Kashmir Willow Cricket Bats",
            category = "Groceries / Food",
            weightTons = 5.5f,
            originCity = "Sangam / Anantnag",
            destinationCity = "Jammu Tawi Freight Hub",
            routeId = "nh44_highway",
            baseRewardInr = 34000L,
            fragility = 0.40f,
            timeLimitSec = 230
        ),
        CargoJob(
            id = "job_pashmina_crafts",
            cargoName = "Handmade Pashmina Shawls & Walnut Woodcarvings",
            category = "Groceries / Food",
            weightTons = 3.2f,
            originCity = "Downtown Srinagar",
            destinationCity = "Gulmarg Resort Grand Hotel",
            routeId = "gulmarg_pass",
            baseRewardInr = 52000L,
            fragility = 0.80f,
            timeLimitSec = 260
        ),
        CargoJob(
            id = "job_petroleum_tanker",
            cargoName = "High Altitude Winter Diesel (Indian Oil Tanker)",
            category = "Forestry / Oil",
            weightTons = 15.5f,
            originCity = "IOCL Banihal Base",
            destinationCity = "Peer Ki Gali Highway Post",
            routeId = "mughal_road",
            baseRewardInr = 68000L,
            fragility = 0.45f,
            timeLimitSec = 300
        ),
        CargoJob(
            id = "job_tunnel_cement",
            cargoName = "Tunnel Lining Cement & Steel Girders (NH-44 Infra)",
            category = "Heavy Industry",
            weightTons = 19.8f,
            originCity = "Qazigund Rail Head",
            destinationCity = "Peer Ki Gali Tunnel Portal",
            routeId = "mughal_road",
            baseRewardInr = 76000L,
            fragility = 0.30f,
            timeLimitSec = 360
        ),
        CargoJob(
            id = "job_deodar_timber",
            cargoName = "Himalayan Cedar & Pine Timber Logs (Gelondongan)",
            category = "Forestry / Oil",
            weightTons = 23.5f,
            originCity = "Kupwara Pine Forest",
            destinationCity = "Srinagar Timber Yard",
            routeId = "mughal_road",
            baseRewardInr = 84000L,
            fragility = 0.25f,
            timeLimitSec = 390
        ),
        CargoJob(
            id = "job_tunnel_machinery",
            cargoName = "Zojila Highway Tunnel Boring Drill Machine Parts",
            category = "Heavy Industry",
            weightTons = 26.5f,
            originCity = "Jammu Railway Goods Yard",
            destinationCity = "Sonamarg Project Base",
            routeId = "nh44_highway",
            baseRewardInr = 95000L,
            fragility = 0.35f,
            timeLimitSec = 420
        )
    )

    val ROUTE_FUEL_STATIONS = mapOf(
        "nh44_highway" to listOf(
            FuelStationInfo("spbu_1", "Indian Oil - NH-44 Banihal Rest Stop", 750f, 90f, true, "left"),
            FuelStationInfo("spbu_2", "Bharat Petroleum - Qazigund Expressway Bay", 1650f, 90f, true, "right"),
            FuelStationInfo("spbu_3", "HPCL - Jawahar Tunnel South Portal", 2450f, 90f, false, "left")
        ),
        "mughal_road" to listOf(
            FuelStationInfo("spbu_m1", "Indian Oil - Shopian High Altitude Pump", 700f, 92f, true, "left"),
            FuelStationInfo("spbu_m2", "Himalayan Fuel - Peer Ki Gali Base Camp", 1850f, 92f, true, "right")
        ),
        "zojila_pass" to listOf(
            FuelStationInfo("spbu_z1", "Indian Oil - Sonamarg Gateway Fuel Bay", 800f, 91f, true, "left"),
            FuelStationInfo("spbu_z2", "Bharat Petroleum - Dras Mountain Rest Point", 1950f, 91f, true, "right")
        ),
        "gulmarg_pass" to listOf(
            FuelStationInfo("spbu_g1", "HPCL - Tangmarg Pine Forest Station", 850f, 91f, true, "left"),
            FuelStationInfo("spbu_g2", "Indian Oil - Gulmarg Pass Resort Bay", 1900f, 91f, true, "right")
        )
    )

    val ROUTE_TURNS = mapOf(
        "mughal_road" to listOf(
            RouteTurnPoint(220f, "Peer Ki Gali Hairpin 1", "hairpin_right", -85f, 25, 18, "Swing wide right, avoid inside mountain stall!"),
            RouteTurnPoint(580f, "Pir Panjal Steep Incline", "hairpin_left", 80f, 20, 22, "Extreme 22% grade! Downshift to 1st/2nd gear now!"),
            RouteTurnPoint(1050f, "Mughal Road S-Bend", "s_curve", -60f, 35, 16, "Keep throttle steady through double S-curves."),
            RouteTurnPoint(1600f, "Bafliaz Cliffside Bend", "sharp_right", -75f, 20, 20, "Deep cliff gorge drop-off on right. Watch oncoming mountain taxis!"),
            RouteTurnPoint(2150f, "Peer Ki Gali Summit Hairpin", "hairpin_left", 85f, 20, 15, "Final steep summit bend at 3,490m. Maintain high RPM."),
            RouteTurnPoint(2700f, "Aliabad Sarai Crest", "gentle_right", -30f, 45, 6, "Road levels out. Prepare for terminal delivery!")
        ),
        "zojila_pass" to listOf(
            RouteTurnPoint(180f, "Zojila Baltal Hairpin 1", "hairpin_left", 75f, 30, 10, "Watch for melting ice and gravel!"),
            RouteTurnPoint(450f, "Sonamarg Gorge Bend", "hairpin_right", -75f, 30, 12, "Cliff drop on outer lane."),
            RouteTurnPoint(750f, "Gumri Valley Bend", "hairpin_left", 80f, 25, 14, "Narrow granite passage."),
            RouteTurnPoint(1080f, "Minamarg Cliffside", "hairpin_right", -80f, 25, 12, "Downshift to maintain engine braking."),
            RouteTurnPoint(1420f, "Zojila Zero Point Pass", "hairpin_left", 75f, 30, 8, "Snow walls on both sides!"),
            RouteTurnPoint(2150f, "Dras Valley Gateway", "hairpin_left", 80f, 28, 12, "Approaching military transit checkpoint.")
        ),
        "nh44_highway" to listOf(
            RouteTurnPoint(450f, "Banihal Expressway Curve 1", "gentle_right", -30f, 65, 2, "Smooth highway sweeping bend."),
            RouteTurnPoint(1150f, "Jawahar Tunnel Approach", "gentle_left", 35f, 60, 4, "Turn on headlights before tunnel entry!"),
            RouteTurnPoint(1850f, "Qazigund Bypass Bridge", "sharp_right", -50f, 50, 2, "Watch heavy truck traffic crossing viaduct."),
            RouteTurnPoint(2500f, "Anantnag Valley Straight", "gentle_left", 30f, 65, 1, "High-speed national corridor straightaway.")
        ),
        "gulmarg_pass" to listOf(
            RouteTurnPoint(280f, "Tangmarg Pine Orchard Bend", "sharp_right", -55f, 35, 12, "Scenic winding curve through tall pine trees."),
            RouteTurnPoint(650f, "Gulmarg Mountain Curve", "sharp_left", 60f, 30, 15, "Steep climb towards ski resort elevation."),
            RouteTurnPoint(1100f, "Apharwat Peak S-Loop", "s_curve", -50f, 35, 14, "Misty hairpins with snow flurries."),
            RouteTurnPoint(1550f, "Baba Reshi Ridge Curve", "sharp_right", -65f, 30, 12, "Keep truck in 2nd/3rd gear on steady ascent.")
        )
    )

    val RADIO_STATIONS = listOf(
        RadioStation(
            id = "radio_chinar",
            name = "Radio Chinar 90.4 FM",
            frequency = "90.4 FM",
            genre = "Kashmir Folk",
            tagline = "Sufiyana Kalam, Rouf & Valley Beats",
            currentProgram = "Valley Melodies for Night Highway Drivers",
            bpm = 136
        ),
        RadioStation(
            id = "highway_beats",
            name = "Highway Beats 98.3 FM",
            frequency = "98.3 FM",
            genre = "Highway Hits",
            tagline = "Top Bollywood & Dhol Highway Anthems",
            currentProgram = "Non-Stop Mountain Expressway Hits",
            bpm = 112
        ),
        RadioStation(
            id = "kashmir_samachar",
            name = "Kashmir Samachar 93.5 FM",
            frequency = "93.5 FM",
            genre = "Traffic & Weather",
            tagline = "NH-44 Advisory, Jawahar Tunnel & Weather",
            currentProgram = "NH-44 & Mughal Road 24/7 Traffic Control Desk",
            bpm = 96
        )
    )

    val ACHIEVEMENTS = listOf(
        AchievementBadge(
            id = "dist_rookie_3k",
            title = "NH-44 Highway Rookie",
            kashmirTitle = "Kashmir Valley Cadet",
            description = "Log your first 3 kilometers of mountain highway driving on Kashmir routes.",
            category = "distance",
            rarity = "bronze",
            targetValue = 3000L,
            unit = "m",
            rewardMoneyInr = 25000L,
            rewardXp = 50L
        ),
        AchievementBadge(
            id = "dist_intercity_10k",
            title = "Pir Panjal Cruiser",
            kashmirTitle = "Pir Panjal Voyager",
            description = "Travel a cumulative 10 kilometers across high altitude mountain corridors.",
            category = "distance",
            rarity = "silver",
            targetValue = 10000L,
            unit = "m",
            rewardMoneyInr = 60000L,
            rewardXp = 120L
        ),
        AchievementBadge(
            id = "dist_transkashmir_25k",
            title = "Trans-Kashmir Long Hauler",
            kashmirTitle = "Banihal-Srinagar Hauler",
            description = "Accumulate 25 kilometers of heavy hauling across Jammu, Banihal & Srinagar trunk corridors.",
            category = "distance",
            rarity = "gold",
            targetValue = 25000L,
            unit = "m",
            rewardMoneyInr = 150000L,
            rewardXp = 250L
        ),
        AchievementBadge(
            id = "dist_himalayan_60k",
            title = "Himalayan Highway Legend",
            kashmirTitle = "Sultan of the Himalayas",
            description = "Conquer 60 kilometers of treacherous Kashmiri mountain passes and steep inclines.",
            category = "distance",
            rarity = "diamond",
            targetValue = 60000L,
            unit = "m",
            rewardMoneyInr = 350000L,
            rewardXp = 600L
        ),
        AchievementBadge(
            id = "job_first_contract",
            title = "First Valley Delivery",
            kashmirTitle = "First Safe Consignment",
            description = "Successfully deliver your first commercial Kashmiri cargo contract to its depot.",
            category = "jobs",
            rarity = "bronze",
            targetValue = 1L,
            unit = "Jobs",
            rewardMoneyInr = 15000L,
            rewardXp = 40L
        ),
        AchievementBadge(
            id = "job_trusted_5",
            title = "Reliable Mountain Courier",
            kashmirTitle = "Trusted Valley Expeditor",
            description = "Complete 5 cargo delivery jobs on time without mission abandonment.",
            category = "jobs",
            rarity = "silver",
            targetValue = 5L,
            unit = "Jobs",
            rewardMoneyInr = 75000L,
            rewardXp = 150L
        ),
        AchievementBadge(
            id = "job_master_12",
            title = "Kashmir Fleet Master",
            kashmirTitle = "Logistics Fleet King",
            description = "Successfully complete 12 commercial logistics contracts across the mountain passes.",
            category = "jobs",
            rarity = "gold",
            targetValue = 12L,
            unit = "Jobs",
            rewardMoneyInr = 200000L,
            rewardXp = 400L
        ),
        AchievementBadge(
            id = "job_clean_pristine",
            title = "Pristine Saffron & Apple Courier",
            kashmirTitle = "Pristine Cargo Driver",
            description = "Complete 3 cargo deliveries with 0% cargo damage on rocky roads.",
            category = "jobs",
            rarity = "gold",
            targetValue = 3L,
            unit = "Clean Jobs",
            rewardMoneyInr = 100000L,
            rewardXp = 200L
        ),
        AchievementBadge(
            id = "oleng_starter_2k",
            title = "Pahadi Sway Apprentice",
            kashmirTitle = "Hairpin Sway Apprentice",
            description = "Reach a high-speed body sway and drift score of 2,000 on winding roads.",
            category = "oleng",
            rarity = "bronze",
            targetValue = 2000L,
            unit = "Pts",
            rewardMoneyInr = 20000L,
            rewardXp = 50L
        ),
        AchievementBadge(
            id = "oleng_expert_10k",
            title = "Sultan of Mughal Road",
            kashmirTitle = "Mughal Road Drift Master",
            description = "Execute rapid alternating mountain hairpins reaching 10,000 sway points.",
            category = "oleng",
            rarity = "silver",
            targetValue = 10000L,
            unit = "Pts",
            rewardMoneyInr = 80000L,
            rewardXp = 180L
        ),
        AchievementBadge(
            id = "oleng_legend_25k",
            title = "Supreme Himalayan Master",
            kashmirTitle = "Supreme Mountain King",
            description = "Achieve an elite 25,000 mountain sway score with flawless rhythmic momentum.",
            category = "oleng",
            rarity = "diamond",
            targetValue = 25000L,
            unit = "Pts",
            rewardMoneyInr = 250000L,
            rewardXp = 500L
        ),
        AchievementBadge(
            id = "garage_fleet_2",
            title = "Valley Fleet Expansion",
            kashmirTitle = "Double Truck Fleet",
            description = "Purchase and own at least 2 distinct truck models in your garage.",
            category = "garage",
            rarity = "bronze",
            targetValue = 2L,
            unit = "Trucks",
            rewardMoneyInr = 50000L,
            rewardXp = 100L
        ),
        AchievementBadge(
            id = "spbu_regular_3",
            title = "Indian Oil Highway Regular",
            kashmirTitle = "Highway Refuel Patron",
            description = "Refuel at highway Indian Oil & BPCL petrol pumps 3 times.",
            category = "endurance",
            rarity = "bronze",
            targetValue = 3L,
            unit = "Refuels",
            rewardMoneyInr = 30000L,
            rewardXp = 75L
        )
    )

    val DRIVER_TITLES = listOf(
        "Valley Novice Driver",
        "Highway Route Hauler",
        "Pass Navigator (Pir Panjal)",
        "Tunnel Specialist (Jawahar / Navyug)",
        "Master of Zojila Pass",
        "Veteran Himalayan Convoy Captain",
        "High Altitude Mountain Ace",
        "Peer Ki Gali Sultan",
        "King of Mughal Road",
        "Legendary Kashmir Highway Master"
    )
}
