package movement;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class UniversityParts {
    static final List<Coord> north_walls = Arrays.asList(
        new Coord(162, 205),    // 270.343
        new Coord(122, 205),    // 204.343
        new Coord(122, 115),    // 204.194
        new Coord(139, 115),    // 233.194
        new Coord(139, 190),    // 233.317
        new Coord(174, 190),    // 290.317
        new Coord(174, 115),    // 290.193
        new Coord(192, 115),    // 320.193
        new Coord(192, 193),    // 320.323
        new Coord(226, 193),    // 377.323
        new Coord(226, 115),    // 377.193
        new Coord(243, 115),    // 406.193
        new Coord(243, 190),    // 406.317
        new Coord(278, 190),    // 464.317
        new Coord(278, 115),    // 464.193
        new Coord(295, 115),    // 493.193
        new Coord(295, 176),    // 493.294
        new Coord(330, 176),    // 551.294
        new Coord(330, 115),    // 551.193
        new Coord(348, 115),    // 580.193
        new Coord(348, 201),    // 580.335
        new Coord(360, 201),    // 601.335
        new Coord(360, 200),    // 601.334
        new Coord(347, 200),    // 581.334
        new Coord(347, 114),    // 581.192
        new Coord(329, 114),    // 550.192
        new Coord(329, 175),    // 550.293
        new Coord(296, 175),    // 494.293
        new Coord(296, 114),    // 494.192
        new Coord(277, 114),    // 463.192
        new Coord(277, 189),    // 463.316
        new Coord(244, 189),    // 407.316
        new Coord(244, 114),    // 407.192
        new Coord(225, 114),    // 376.192
        new Coord(225, 192),    // 376.322
        new Coord(193, 192),    // 321.322
        new Coord(193, 114),    // 321.192
        new Coord(173, 114),    // 289.192
        new Coord(173, 189),    // 289.316
        new Coord(140, 189),    // 234.316
        new Coord(140, 114),    // 234.193
        new Coord(121, 114),    // 203.193
        new Coord(121, 206),    // 203.344
        new Coord(161, 206)     // 269.344
        );

    static final List<Coord> east_walls = Arrays.asList(
            new Coord(370, 201),    // 618.335
            new Coord(386, 201),    // 644.335
            new Coord(409, 190),    // 682.316
            new Coord(423, 195),    // 707.325
            new Coord(430, 206),    // 715.344
            new Coord(420, 222),    // 700.372
            new Coord(403,230),    // 670.385
            new Coord(402, 231),    // 671.386
            new Coord(420, 223),    // 700.373
            new Coord(429, 206),    // 716.344
            new Coord(424, 194),    // 708.324
            new Coord(409, 190),    // 682.317
            new Coord(386, 200),    // 644.334
            new Coord(370, 200)     // 618.334
    );

    static final List<Coord> south_walls = Arrays.asList(
            new Coord(403, 237),    // 670.399
            new Coord(419, 237),    // 699.399
            new Coord(412, 317),    // 688.530
            new Coord(395, 316),    // 658.528
            new Coord(397, 251),    // 662.420
            new Coord(394, 252),    // 658.421
            new Coord(392, 257),    // 654.430
            new Coord(385, 262),    // 642.435
            new Coord(378, 257),    // 630.429
            new Coord(376, 249),    // 626.416
            new Coord(369, 249),    // 617.416
            new Coord(363, 313),    // 607.523
            new Coord(347, 312),    // 578.521
            new Coord(352, 246),    // 587.413
            new Coord(346, 246),    // 578.413
            new Coord(344, 253),    // 574.423
            new Coord(337, 256),    // 562.428
            new Coord(329, 251),    // 548.419
            new Coord(328, 245),    // 546.409
            new Coord(321, 245),    // 537.409
            new Coord(315, 308),    // 527.515
            new Coord(299, 307),    // 498.513
            new Coord(301, 240),    // 502.402
            new Coord(275, 239),    // 459.399
            new Coord(268, 304),    // 447.508
            new Coord(251, 302),    // 418.505
            new Coord(256, 243),    // 427.406
            new Coord(252, 243),    // 421.406
            new Coord(251, 251),    // 419.420
            new Coord(229, 249),    // 381.416
            new Coord(229, 241),    // 382.402
            new Coord(225, 241),    // 376.402
            new Coord(220, 299),    // 368.500
            new Coord(203, 298),    // 338.498
            new Coord(209, 225),    // 348.375
            new Coord(184, 223),    // 308.372
            new Coord(183, 264),    // 307.441
            new Coord(127, 264),    // 214.441
            new Coord(128, 217),    // 214.362
            new Coord(162, 217),    // 270.361
            new Coord(162, 216),    // 270.360
            new Coord(127, 217),    // 213.361
            new Coord(127, 265),    // 213.442
            new Coord(184, 265),    // 308.442
            new Coord(185, 224),    // 309.373
            new Coord(208, 226),    // 347.376
            new Coord(202, 299),    // 337.499
            new Coord(221, 300),    // 369.501
            new Coord(226, 242),    // 377.403
            new Coord(228, 242),    // 381.403
            new Coord(228, 250),    // 380.417
            new Coord(252, 252),    // 420.421
            new Coord(253, 244),    // 422.407
            new Coord(255, 244),    // 426.407
            new Coord(250, 303),    // 417.506
            new Coord(268, 305),    // 448.509
            new Coord(276, 240),    // 460.400
            new Coord(300, 241),    // 501.403
            new Coord(298, 308),    // 497.514
            new Coord(316, 309),    // 528.516
            new Coord(322, 246),    // 538.410
            new Coord(327, 246),    // 545.410
            new Coord(328, 252),    // 547.420
            new Coord(337, 257),    // 562.429
            new Coord(345, 254),    // 575.424
            new Coord(347, 248),    // 579.414
            new Coord(351, 248),    // 586.414
            new Coord(346, 313),    // 577.522
            new Coord(364, 314),    // 608.524
            new Coord(370, 250),    // 618.417
            new Coord(375, 250),    // 625.417
            new Coord(377, 258),    // 629.430
            new Coord(385, 261),    // 642.436
            new Coord(393, 258),    // 655.431
            new Coord(395, 253),    // 659.422
            new Coord(396, 252),    // 661.421
            new Coord(394, 317),    // 657.529
            new Coord(413, 318),    // 689.531
            new Coord(420, 238),    // 700.398
            new Coord(402, 238)    // 671.398
    );
}