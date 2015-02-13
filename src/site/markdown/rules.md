== Basic rules ==
A set of rules suitable for beginners is presented here. In some respects, these differ from the rules most commonly used. However, the basic rules are simply stated, and provide a convenient basis on which to discuss differences in rulesets. The rules are studied more fully in [[#Explanation of the basic rules|Explanation of the basic rules]] below.

Two statements of the same basic rules, differing only in wording, are given here. The first is a concise one due to James Davies. The second is a formulation of the basic rules used for expository purposes in this article.

Except for terminology, the basic rules are identical to the [http://homepages.cwi.nl/~tromp/go.html Logical Rules] first proposed in their current form in September 1996 by John Tromp and Bill Taylor.<ref>[http://home.snafu.de/jasiek/superko.html Super Ko], Robert Jasiek</ref><ref>[http://home.snafu.de/jasiek/intro.html Commentary on Tromp-Taylor Rules], Robert Jasiek</ref> They are also quite close to the [http://home.snafu.de/jasiek/siming.html Simplified Ing Rules] of the [[European Go Federation]], the only exception being the method of ending the game.

=== Concise statement ===
These rules appear in "The Rules and Elements of Go" by James Davies.<ref>[http://home.snafu.de/jasiek/element.html Elementary Rules] of James Davies</ref> They assume familiarity with the equipment used to play go, for which one may refer to [[#Elements of the game|Elements of the game]] below.

''Notes:'' The words ''move'' and ''territory'' are used differently here than elsewhere in this article; [[#Moving|''play'']] and [[#Area|''area'']], respectively, are used instead. A clarification to rule 5 is added in parentheses.
# The [[#Board|board]] is empty at the onset of the game (unless players agree to place a handicap).
# [[#Players|Black]] makes the first move, after which [[#Players|White]] and [[#Players|Black]]  alternate.
# A [[#Moving|move]] consists of placing one [[#Stones|stone]] of one's own color on an empty [[#Board|intersection]] on the board.
# A player may [[#Moving|pass his turn]] at any time, but must sacrifice one of their prisoners (,captured pieces).
# A stone or solidly [[#Connected stones and points|connected]] group of stones of one color is captured and removed from the board when all the intersections directly [[#Board|adjacent]] to it are occupied by the enemy. ([[#Capture|Capture]] of the enemy takes precedence over [[#Self-capture|self-capture]].)
# No stone may be played so as to recreate a former board [[#Positions|position]].
# Two consecutive passes end the game. However, since black begins, white must end the game.
# A player's [[#Area|territory]] consists of all the [[#Board|points]] the player has either occupied or [[#Territory|surrounded]].
# The player with more territory wins.

These rules rely on common sense to make notions such as "connected group" and "surround" precise. What is here called a "solidly connected group of stones" is also called a [[#Connected stones and points|''chain'']].

===Reference statement===
The basic rules are formulated here in a more detailed way to ease their presentation in the section [[#Explanation of the basic rules]] below. (Each rule and definition links to a detailed explanation in that section.)

An optional rule prohibiting suicide is included as Rule 7A.

====Players and equipment====
* [[#Players|Rule 1]].<ref>[http://www.cs.cmu.edu/~wjh/go/rules/AGA.html Official AGA Rules of Go], Rule 1: "The two sides [are] known as ''Black'' and ''White''[...]"</ref> '''Players:''' Go is a game between two players, called Black and White.
* [[#Board|Rule 2]].<ref>[http://www.cs.cmu.edu/~wjh/go/rules/AGA.html Official AGA Rules of Go], Rule 1: "Go is a game of strategy between two sides usually played on a 19x19 grid (the ''board'')."</ref> '''Board:''' Go is played on a plain grid of 19 horizontal and 19 vertical lines, called a ''board''.
** [[#Board|Definition]].<ref>[http://homepages.ihug.co.nz/~barryp/rules.htm New Zealand Go Society Rules of Go]: "''Adjacent'' intersections are those intersections connected by lines of the grid, with no intervening intersections."</ref> '''("Intersection", "Adjacent")''' A point on the board where a horizontal line meets a vertical line is called an ''intersection''. Two intersections are said to be ''adjacent'' if they are connected by a horizontal or vertical line with no other intersections between them.
* [[#Stones|Rule 3]].<ref>[http://www.cs.cmu.edu/~wjh/go/rules/AGA.html Official AGA Rules of Go], Rule 1: "The two sides, known as ''Black'' and ''White'', are each provided with an adequate supply of playing tokens, known as ''stones'', of the appropriate color."</ref> <ref>[http://www.cs.cmu.edu/~wjh/go/rules/Chinese.html Rules of Go], Section 1.2 "Lens-shaped black and white stones are used. The number of stones is preferably 180 of each color. "</ref> '''Stones:''' Go is played with playing tokens known as ''stones''. Each player has at their disposal an adequate supply (usually 180) of stones of the same color.

====Positions====
* [[#Positions|Rule 4]].<ref>[http://tromp.github.io/go.html Tromp-Taylor rules of go]: "Each point on the grid may be colored black, white or empty."</ref><ref>[http://home.snafu.de/jasiek/siming.html Simplified Ing Rules] of the EGF: "The position is the distribution of black, white, and no stones on all the unique intersections of the grid. For a play, this is given after all its removals."</ref> '''Positions:''' At any time in the game, each intersection on the board is in one and only one of the following three states: 1) empty; 2) occupied by a black stone; or 3) occupied by a white stone. A ''position'' consists of an indication of the state of each intersection.
** [[#Connected stones and points|Definition]].<ref>[http://home.snafu.de/jasiek/siming.html Simplified Ing Rules] of the EGF: "Stones of the same colour are connected if they are adjacent or if there is a chain of adjacent stones of their colour between them. Likewise, empty intersections are connected if they are adjacent or if there is a chain of adjacent empty intersections between them."</ref> '''("Connected")''' Two placed stones of the same color (or two empty intersections) are said to be ''connected'' if it is possible to draw a path from one position to the other by passing through adjacent positions of the same state (empty, occ. by white, or occ. by black).
** [[#Liberties|Definition]].<ref>[http://homepages.ihug.co.nz/~barryp/rules.htm New Zealand Go Society Rules of Go]: "A ''liberty'' of a stone is an unoccupied intersection adjacent to that stone or to any stone connected to that stone."</ref> '''("Liberty")''' In a given position, a ''liberty'' of a stone is an empty intersection adjacent to that stone or adjacent to a stone which is connected to that stone.

====Play====
* [[#Initial position|Rule 5]].<ref>[http://www.cs.cmu.edu/~wjh/go/rules/AGA.html Official AGA Rules of Go], Rule 1: "The board is initially vacant [...]"</ref> '''Initial position:''' At the beginning of the game, the board is empty.
* [[#Alternation of turns|Rule 6]].<ref>[http://www.cs.cmu.edu/~wjh/go/rules/AGA.html Official AGA Rules of Go], Rule 2: "The players alternate in moving, with Black playing first."</ref> '''Turns:''' Black moves first. The players alternate thereafter.
* [[#Moving|Rule 7]].<ref>[http://homepages.ihug.co.nz/~barryp/rules.htm New Zealand Go Society Rules of Go]: "A ''play'' consists of placing a stone (of that player's own colour) on an unoccupied intersection, then removing any of the opponent's stones that then have no liberties (if any), and then removing any of that player's own stones that then have no liberties (if any). A ''move'' consists of 1. making a play [...] or 2. saying 'pass'."</ref> '''Moving:''' When it is their turn, a player may either ''pass'' (by announcing "pass" and performing no action) or ''play''. A play consists of the following steps (performed in the prescribed order):
** [[#Placing a stone on the board|Step 1]]. (Playing a stone) Placing a stone of their color on an empty intersection (chosen subject to Rule 8 and, if it is in effect, to Optional Rule 7A). It can never be moved to another intersection after being played.
** [[#Capture|Step 2]]. (Capture) Removing from the board any stones of their opponent's color that have no liberties.
** [[#Self-capture|Step 3]]. (Self-capture) Removing from the board any stones of their own color that have no liberties.
* ''[[#Self-capture|Optional Rule 7A]].''<ref>[http://www.cs.cmu.edu/~wjh/go/rules/AGA.html Official AGA Rules of Go], Rule 5: "It is ''illegal'' for a player to move so as to create a string of their own stones which is completely surrounded (without liberties) after any surrounded opposing stones are captured."</ref> ''Prohibition of suicide: A play is illegal if one or more stones of that player's color would be removed in Step 3 of that play.''
* [[#Ko|Rule 8]].<ref>[http://home.snafu.de/jasiek/siming.html Simplified Ing Rules] of the EGF: "A play may not recreate a previous position from the game."</ref> '''Prohibition of repetition:''' A play is illegal if it would have the effect (after all steps of the play have been completed) of creating a position that has occurred previously in the game.

====End====
* [[#End|Rule 9]].<ref>[http://home.snafu.de/jasiek/element.html Elementary Rules] of James Davies: "Two consecutive passes end the game."</ref> '''End:''' The game ends when both players have passed consecutively. The ''final position'' is the position on the board at the time the players pass consecutively.
** [[#Territory|Definition]].<ref>[http://www.cs.cmu.edu/~wjh/go/rules/AGA.html Official AGA Rules of Go], Rule 12: "Territory: Those empty points on the board which are entirely surrounded by live stones of a single color are considered the ''territory'' of the player of that color."</ref><ref>Though the Simplified Ing Rules use the word "territory" differently, they describe what is here defined to be a player's territory as consisting of "the empty regions that are adjacent only to intersections with stones of a player's colour." The Commentary to the rules further specifies: "During scoring, an empty region does not provide any points if a) it is adjacent to at least one black intersection and adjacent to at least one white intersection or b) the whole board is empty."</ref> '''("Territory")''' In the final position, an empty intersection is said to belong to a player's ''territory'' if, after all dead stones are removed, all stones adjacent to it or to an empty intersection connected to it are of that player's color.
** [[#Area|Definition]].<ref>[http://www.cs.cmu.edu/~wjh/go/rules/AGA.html Official AGA Rules of Go], Rule 12: "Area: All live stones of a player's color left on the board together with any points of territory surrounded by a player constitute that player's ''area''."</ref> '''("Area")''' In the final position, an intersection is said to belong to a player's ''area'' if either: 1) it belongs to that player's territory; or 2) it is occupied by a stone of that player's color.
** [[#Score|Definition]].<ref>[http://home.snafu.de/jasiek/siming.html Simplified Ing Rules] of the EGF: "The ''score'' of each player is the number of all intersections a) with stones of the player's color, and b) of the empty regions that are adjacent only to intersections with stones of the player's color."</ref> '''("Score")''' A player's ''score'' is the number of intersections in their area in the final position.
* [[#Winner|Rule 10]].<ref>[http://home.snafu.de/jasiek/siming.html Simplified Ing Rules] of the EGF: "For the final position, either the scores are unequal and the winner is the player with the greater score or the scores are equal and the game is a tie."</ref> '''Winner:''' If one player has a higher score than the other, then that player wins. Otherwise, the game is drawn.

=== Comparative features of the basic rules ===
The essential features of these basic rules relative to other rulesets are summarized here. Each of the differences is discussed in greater detail in a later section of the article.

What variation exists among rulesets concerns primarily Rules 7A, 8, 9 and 10.

* The basic rules use '''area scoring''', as in China and Taiwan, and as in the official rules of many Western countries. The main alternative is '''territory scoring'''. Though territory scoring is the system used in Japan and Korea, and is customarily used in the West, it is not possible to use territory scoring unless Rule 9 is replaced by a much more complex end-of-game rule. The goal of these basic rules is to present a simple system first. See the section "[[Rules_of_Go#Scoring systems|Scoring systems]]" below.

* The basic rules, require the players to "play the game out" entirely, capturing all dead stones in normal play. Virtually all rulesets used in practice provide some mechanism that allows players to begin scoring the game before the final position (the one used to score the game) has been reached. In some cases, this is merely a convenience intended to save time. In others, it may be an essential feature of the game. In any case, explaining these rules might obscure the nature of the game somewhat for a person unfamiliar with it. See the section "[[Rules_of_Go#Counting phase|Counting phase]]" below.

* The basic rules allow ''suicide'' (or ''self-capture''). This is unusual outside of Taiwan and New Zealand. Inclusion of Optional Rule 7A is in line with practice elsewhere. See the section "[[Rules_of_Go#Suicide|Suicide]]" below.

* The basic rules apply the rule of ''positional superko''. This, or a similar rule, is common in official Western rulesets, but not in East Asia. See the section "[[Rules_of_Go#Repetition|Repetition]]" below.

* The basic rules do not contain any special exceptions for territory in a ''seki''. This agrees with most practice outside Japan and Korea. See the section "[[Rules_of_Go#Seki|Seki]]" below.

* The basic rules do not have a ''komi''. This is now unusual in even-strength games, but was common practice until the mid-twentieth century. A ''komi'' is a number of points, usually five to eight, awarded to White in compensation for moving second. See the section "[[Rules_of_Go#Komi|Komi]]" below.

* The basic rules make no provision for the use of ''handicap stones''. See the section "[[Rules_of_Go#Handicap|Handicap]]" below.

* The basic rules do not specify a ''counting system''. A counting system is a conventional method for calculating the difference in score between the players (hence determining the winner). It may incorporate various devices, such as filling in one's territory after the game, or shifting stones on the board into patterns, which allow quicker calculation of the difference in scores.

This page is a copy from Wikipedia page http://en.wikipedia.org/wiki/Rules_of_Go