import tester.*;                // The tester library
import javalib.worldimages.*;   // images, like RectangleImage or OverlayImages
import javalib.funworld.*;      // the abstract World class and the big-bang library
import java.awt.Color;          // general colors (as triples of red,green,blue values)
import java.util.Random;
// and predefined colors (Red, Green, Yellow, Blue, Black, White)

// Represents a world class to animate Words, A ship, onto a scene
class ZTypeWorld extends World {
  //World Constants
  final int WIDTH = 400;
  final int HEIGHT = 600;

  ILoWord words; 
  int countTick;

  ZTypeWorld(ILoWord words, int countTick) {
    this.words = words;
    this.countTick = countTick;
  }

  /*TEMPLATE
   * Fields:
   *  this.words ... ILoWord
   *  this.countTick ... integer
   * Methods:
   *  this.makeScene ... WorldScene
   *  this.onTick ... World
   *  this.onTickForTesting ... World
   *  this.onKeyEvent ... World
   *  this.worldEnds ... WorldEnd
   *  this.makeAFinalScene ... WorldScene
   * Methods of fields:
   *  this.words.makeScene ... WorldScene
   *  this.words.onTick ... World
   *  this.words.onTickForTesting ... World
   *  this.words.onKeyEvent ... World
   *  this.words.worldEnds ... WorldEnd
   *  this.words.makeAFinalScene ... WorldScene
   */

  RectangleImage background = new RectangleImage(WIDTH, HEIGHT, OutlineMode.SOLID, Color.BLACK);
  OverlayOffsetImage rocket1 = new OverlayOffsetImage(new TriangleImage(
      new Posn(25, 0), new Posn(0, 25), new Posn(50, 25), "solid", Color.RED), 
      0, 30, 
      (new EllipseImage(30, 60, OutlineMode.SOLID, Color.LIGHT_GRAY)));

  OverlayOffsetImage rocket2 = new OverlayOffsetImage(
      new CircleImage(10, "solid", Color.BLUE), 0, 0, 
      rocket1);

  OverlayOffsetImage rocket3 = new OverlayOffsetImage(new TriangleImage(
      new Posn(15, 0), new Posn(0, 15), new Posn(15, 15), 
      "solid", Color.RED), 15, -30, rocket2);

  OverlayOffsetImage rocket4 = new OverlayOffsetImage(new TriangleImage(
      new Posn(-15, 0), new Posn(-15, 15), new Posn(0, 15), 
      "solid", Color.RED), -15, -30, rocket3);

  OverlayImage rocket5 = new OverlayImage(
      new StarImage(50, 7, OutlineMode.SOLID, Color.ORANGE), rocket4);


  // Make the scene of words falling down
  public WorldScene makeScene() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.words.draw(
        new WorldScene(WIDTH, HEIGHT)
        .placeImageXY(rocket4, 200, 520)); 
  }

  // Random words fall at random coordinates of x 
  // at the top of the scene (0 y-coordinates)
  // at the rate of 15 ticks per second
  public World onTick() {
    //EVERYTHING IN THE CLASS TEMPLATE
    Utils u = new Utils();
    Random rand = new Random();
    ILoWord add =
        new ConsLoWord(new InactiveWord(u.randomWord(rand), u.restrictX(), 0), this.words);

    if (this.countTick % 15 == 0) {
      return new ZTypeWorld(add.moveWord(), this.countTick + 1); 
    } else {
      return new ZTypeWorld(words.moveWord(), this.countTick + 1); 
    }
  }

  // Random words fall at random coordinates of x 
  // at the top of the scene (0 y-coordinates)
  // at the rate of 15 ticks per second
  public World onTickForTesting() {
    //EVERYTHING IN THE CLASS TEMPLATE
    Utils u = new Utils();
    Random rand = new Random(20);
    ILoWord add =
        new ConsLoWord(new InactiveWord(u.randomWord(rand), u.restrictX(), 0), this.words);

    if (this.countTick % 15 == 0) {
      return new ZTypeWorld(add.moveWord(), this.countTick + 1); 
    } else {
      return new ZTypeWorld(words.moveWord(), this.countTick + 1); 
    }
  }

  // Check the key pressed and reduce the words falling down
  public World onKeyEvent(String keyEvent) {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (words.anyActive()) {
      return new ZTypeWorld(words.checkAndReduce(keyEvent).filterOutEmpties(), this.countTick);
    } else {
      return new ZTypeWorld(words.makeWordActive(keyEvent), this.countTick);
    }
  }

  // Final scene when the player lost
  // Losing condition: the word touches the ground
  public WorldEnd worldEnds() {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (words.isOut()) {
      return new WorldEnd(true, makeAFinalScene());
    } else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // Final scene when the player lost
  // lost condition is when the words is out (y > HEIGHT)
  public WorldScene makeAFinalScene() {
    //EVERYTHING IN THE CLASS TEMPLATE
    TextImage gameOver = 
        new TextImage("GAME OVER", WIDTH / 10, FontStyle.BOLD, Color.RED);
    return this.makeScene()
        .placeImageXY(gameOver, WIDTH / 2, HEIGHT / 2)
        .placeImageXY(rocket5, 200, 520);
  }
}

//////////////////////////////////////////////////////

//////////////////////////////////////////////////////

//////////////////////////////////////////////////////



// Represents a list of words
interface ILoWord {

  // Sort the word based on the y-coordinates, the lower one gets sorted first
  ILoWord sort();

  // Sort the list, comparing the first two words, then recurse
  ILoWord compareAndSortWord(IWord word);

  // Only take string of length 1
  // Search for all active words, then reduced by removing the first letter
  // That the given string matches with
  ILoWord checkAndReduce(String letter);

  // A WorldScene that draws the words in the list of words
  // Onto the given world scene 
  WorldScene draw(WorldScene world);

  // Move the Word down the y-axis
  ILoWord moveWord(); 

  // Word falls out of game boundary
  boolean isOut();

  // Compute the score of the game, based on the number of empty strings in the game
  // Meaning, the word is fully typed out
  int computeScore();

  // Filter out all the empty word components of the list
  ILoWord filterOutEmpties();

  // Check if the list has any active words
  boolean anyActive();

  // Check if the first word of the list matches with the qualities
  boolean checkFirst(String letter);

  // Activate the word if first letter matches
  ILoWord makeWordActive(String letter);

  // Activate the word if first letter matches
  ILoWord makeFirstActive(String letter);

  // Find the word that is active if first letter does not matches
  ILoWord findActive(String letter);

}

// Represents an empty list of words
class MtLoWord implements ILoWord {
  /*TEMPLATE
   * Methods:
   *  this.sort() ... ILoWord
   *  this.compareAndSortWord(IWord) ... ILoWord
   *  this.checkAndReduce(String) ... ILoWord
   *  this.draw(WorldScene) ... WorldScene
   *  this.moveWord() ... ILoWord
   *  this.isOut() ... boolean
   *  this.computeScore() ... integer
   *  this.filterOutEmpties() ... ILoWord
   *  this.anyActive() ... boolean
   *  this.checkFirst(String) ... boolean
   *  this.makeWordActive(String) ... ILoWord
   *  this.makeFirstActive(String) ... ILoWord
   *  this.findActive(String) ... ILoWord
   */

  // Sort the word based on the y-coordinates, the lower one gets sorted first
  public ILoWord sort() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }

  // Sort the list, comparing the first two words, then recurse
  public ILoWord compareAndSortWord(IWord word) {
    //EVERYTHING IN THE CLASS TEMPLATE, plus, 
    /*Methods of Fields 
     *  this.word.reduceLetter(String) ... IWord
     *  this.word.placeIWordOnWorld(WorldScene) ... WorldScene
     *  this.word.moveWord() ... IWord
     *  this.word.isOut() ... boolean
     *  this.word.isStringEmpty() ... boolean
     *  this.word.checkActiveLetter(String) ... boolean
     *  this.word.isActive() ... boolean
     *  this.word.makeWordActive(String) ... IWord
     *  this.word.checkFirstLetter(String) ... boolean
     *  this.word.compareIWord(IWord) ... boolean
     *  this.word.compareYCoord(integer) ... boolean
     */
    return new ConsLoWord(word, this);
  }

  // Only take string of length 1
  // Search for all active words, then reduced by removing the first letter
  // That the given string matches with
  public ILoWord checkAndReduce(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }

  // A WorldScene that draws all of the words in the list of words
  // Onto the given world scene 
  public WorldScene draw(WorldScene world) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return world;
  }

  // Move the Word down the y-axis
  public ILoWord moveWord() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }

  // Word falls out of game boundary
  public boolean isOut() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return false;
  }

  // Compute the score of the game, based on the number of empty strings in the game
  // Meaning, the word is fully typed out
  public int computeScore() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return 0;
  }

  // Filter out all the empty word components of the list
  public ILoWord filterOutEmpties() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }

  // Check if the list has any active words
  public boolean anyActive() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return false;
  }

  // Check if the first word of the list matches with the qualities
  public boolean checkFirst(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return false;
  }

  // Activate the word if the first letter matches
  public ILoWord makeWordActive(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }

  // Activate the word if first letter matches
  public ILoWord makeFirstActive(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }

  // Find the word that is active if first letter does not matches
  public ILoWord findActive(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }
}

// Represents a list of words
class ConsLoWord implements ILoWord {
  IWord first;
  ILoWord rest;

  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }
  /*TEMPLATE
   * Fields:
   *  this.first ... IWord
   *  this.rest ... ILoWord
   * 
   * Methods:
   *  this.sort() ... ILoWord
   *  this.compareAndSortWord(IWord) ... ILoWord
   *  this.checkAndReduce(String) ... ILoWord
   *  this.draw(WorldScene) ... WorldScene
   *  this.moveWord() ... ILoWord
   *  this.isOut() ... boolean
   *  this.computeScore() ... integer
   *  this.filterOutEmpties() ... ILoWord
   *  this.anyActive() ... boolean
   *  this.checkFirst(String) ... boolean
   *  this.makeWordActive(String) ... ILoWord
   *  this.makeFirstActive(String) ... ILoWord
   *  this.findActive(String) ... ILoWord
   * 
   * Method of fields:
   *  this.first.reduceLetter(String) ... IWord
   *  this.first.placeIWordOnWorld(WorldScene) ... WorldScene
   *  this.first.moveWord() ... IWord
   *  this.first.isOut() ... boolean
   *  this.first.isStringEmpty() ... boolean
   *  this.first.checkActiveLetter(String) ... boolean
   *  this.first.isActive() ... boolean
   *  this.first.makeWordActive(String) ... IWord
   *  this.first.checkFirstLetter(String) ... boolean
   *  this.first.compareIWord(IWord) ... boolean
   *  this.first.compareYCoord(integer) ... boolean
   *
   *  this.rest.sort() ... ILoWord
   *  this.rest.compareAndSortWord(IWord) ... ILoWord
   *  this.rest.checkAndReduce(String) ... ILoWord
   *  this.rest.draw(WorldScene) ... WorldScene
   *  this.rest.moveWord() ... ILoWord
   *  this.rest.isOut() ... boolean
   *  this.rest.computeScore() ... integer
   *  this.rest.filterOutEmpties() ... ILoWord
   *  this.rest.anyActive() ... boolean
   *  this.rest.checkFirst(String) ... boolean
   *  this.rest.makeWordActive(String) ... ILoWord
   *  this.rest.makeFirstActive(String) ... ILoWord
   *  this.rest.findActive(String) ... ILoWord
   */

  // Produces a new list with words sorted in alphabetical order
  // Checking the first and the first of the rest
  public ILoWord sort() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.rest.sort().compareAndSortWord(this.first);
  }

  // Compare this and the last word, 
  // Then return true if this is <0 or before that
  public ILoWord compareAndSortWord(IWord word) {
    //EVERYTHING IN THE CLASS TEMPLATE, plus, 
    /*Methods of Fields 
     *  this.word.reduceLetter(String) ... IWord
     *  this.word.placeIWordOnWorld(WorldScene) ... WorldScene
     *  this.word.moveWord() ... IWord
     *  this.word.isOut() ... boolean
     *  this.word.isStringEmpty() ... boolean
     *  this.word.checkActiveLetter(String) ... boolean
     *  this.word.isActive() ... boolean
     *  this.word.makeWordActive(String) ... IWord
     *  this.word.checkFirstLetter(String) ... boolean
     *  this.word.compareIWord(IWord) ... boolean
     *  this.word.compareYCoord(integer) ... boolean
     */
    if (this.first.compareIWord(word)) {
      return new ConsLoWord(word, this);
    } else {
      return new ConsLoWord(this.first, rest.compareAndSortWord(word));
    }
  }

  // Only take string of length 1
  // Search for all active words, then reduced by removing the first letter
  // That the given string matches with
  public ILoWord checkAndReduce(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (this.first.checkFirstLetter(letter)) {
      return new ConsLoWord(this.first.reduceLetter(letter), this.rest.checkAndReduce(letter));
    } else {
      return new ConsLoWord(this.first, this.rest.checkAndReduce(letter));
    }
  }

  // A WorldScene that draws all of the words in the list of words
  // Onto the given world scene 
  public WorldScene draw(WorldScene world) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.rest.draw(this.first.placeIWordOnWorld(world));
  }

  // Move the Word down the y-axis
  public ILoWord moveWord() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return new ConsLoWord(this.first.moveWord(), this.rest.moveWord());
  }

  // Check if any IWord falls out of game boundary
  public boolean isOut() {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (this.first.isOut()) {
      return true;
    } else {
      return this.rest.isOut();
    }
  }

  // Compute the score of the game, based on the number of empty strings in the game
  // Meaning, the word is fully typed out
  public int computeScore() {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (this.first.isStringEmpty()) {
      return 50 + this.rest.computeScore();
    } else {
      return 0 + this.rest.computeScore();
    }
  }

  // Filter out all the empty word components of the list
  public ILoWord filterOutEmpties() {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (this.first.isStringEmpty()) { 
      return this.rest.filterOutEmpties();
    } else {
      return new ConsLoWord(this.first, this.rest.filterOutEmpties());
    }
  }

  // Check if the list has any active words
  public boolean anyActive() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.first.isActive() || this.rest.anyActive();
  }

  // Check if the first word of the list matches with the qualities
  public boolean checkFirst(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.first.checkFirstLetter(letter);
  }

  // Activate the word if the first letter matches
  public ILoWord makeWordActive(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (this.sort().checkFirst(letter)) {
      return sort().makeFirstActive(letter);
    } else {
      return sort().findActive(letter);
    }
  }

  // Activate the word if first letter matches
  public ILoWord makeFirstActive(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return new ConsLoWord(this.first.makeWordActive(letter), this.rest);
  }

  // Find the rest of the words if first letter does not matches
  public ILoWord findActive(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return new ConsLoWord(this.first, this.rest.makeWordActive(letter));
  }

}

//////////////////////////////////////////////////////

//////////////////////////////////////////////////////

//////////////////////////////////////////////////////

// Represents a falling word in the ZType game
interface IWord {

  // Reduce the given letter from the string if the word is active
  IWord reduceLetter(String letter);

  // Placing the word on the world
  WorldScene placeIWordOnWorld(WorldScene world);

  // Move the Word down the y-axis
  IWord moveWord();

  // Word falls out of game boundary
  boolean isOut();

  // Check if the string is empty
  boolean isStringEmpty();

  // Check the first letter of the Active word
  boolean checkActiveLetter(String letter);

  // Check if the word is active 
  boolean isActive();

  // Make the word Active
  IWord makeWordActive(String letter);

  // Check the first letter of any word
  boolean checkFirstLetter(String letter);

  // Compare the 2 IWord to see which y coordinate is lower than the other
  boolean compareIWord(IWord word);

  // Compare the y-coordinates of 2 IWord
  boolean compareYCoord(int that);

}

// Abstract for a Word at a location
abstract class AWord implements IWord {
  String word;
  int x;
  int y;
  Random rand;

  // The constructor
  AWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
    this.rand = new Random();
  }

  /*TEMPLATE
   * Fields:
   *  this.word ... String
   *  this.x ... integer
   *  this.y ... integer
   *  this.rand ... Random
   *
   * Method:
   *  this.reduceLetter(String) ... IWord
   *  this.placeIWordOnWorld(WorldScene) ... WorldScene
   *  this.moveWord() ... IWord
   *  this.isOut() ... boolean
   *  this.isStringEmpty() ... boolean
   *  this.checkActiveLetter(String) ... boolean
   *  this.isActive() ... boolean
   *  this.makeWordActive(String) ... IWord
   *  this.checkFirstLetter(String) ... boolean
   *  this.compareIWord(IWord) ... boolean
   *  this.compareYCoord(integer) ... boolean
   */


  // Reduce the given letter from the string IF the word is active
  public IWord reduceLetter(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }

  // Check if the word is out of game boundary
  public boolean isOut() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.y >= 480 && !this.word.isEmpty();
  }

  // Check if the string is empty
  public boolean isStringEmpty() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.word.isEmpty();
  }

  // Check the first letter of the Active word
  public boolean checkActiveLetter(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return false;
  }

  // Check if the word is active 
  public boolean isActive() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return false;
  }

  // Make the word Active if the first letter matches
  // this is where word is already active
  public IWord makeWordActive(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this;
  }

  // Check the first string of the Active word
  public boolean checkFirstLetter(String str) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.checkActiveLetter(str);
  }

  // Compare the 2 IWord to see which coordinate is lower than the other
  public boolean compareIWord(IWord word) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return word.compareYCoord(this.y);
  }

  // Compare the y-coordinates of 2 IWord
  public boolean compareYCoord(int that) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.y > that;
  }

}

// Represents an active word in the ZType game
class ActiveWord extends AWord {
  // the constructor
  ActiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  /*TEMPLATE
   * Fields:
   *  this.word ... String
   *  this.x ... integer
   *  this.y ... integer
   *  this.rand ... Random
   *
   * Method:
   *  this.reduceLetter(String) ... IWord
   *  this.placeIWordOnWorld(WorldScene) ... WorldScene
   *  this.moveWord() ... IWord
   *  this.isOut() ... boolean
   *  this.isStringEmpty() ... boolean
   *  this.checkActiveLetter(String) ... boolean
   *  this.isActive() ... boolean
   *  this.makeWordActive(String) ... IWord
   *  this.checkFirstLetter(String) ... boolean
   *  this.compareIWord(IWord) ... boolean
   *  this.compareYCoord(integer) ... boolean
   */

  // Move the Word down the y-axis
  public IWord moveWord() {
    //EVERYTHING IN THE CLASS TEMPLATE

    // Calculate the direction towards the target point
    // 200 is for the words to fly towards the middle
    // 500 is where the words will stop at the end
    int dx = 200 - this.x;
    int dy = 500 - this.y;

    // Calculate the magnitude of the movement
    double magnitude = Math.sqrt(dx * dx + dy * dy);

    // Calculate the unit vector
    int unitX = (int) Math.round(dx / magnitude);
    int unitY = (int) Math.round(dy / magnitude);

    // Move the dot towards the middle (target point)
    int newX = this.x + unitX * 5; // Adjust the magnitude as needed
    int newY = this.y + unitY * 5;

    return new ActiveWord(this.word, newX, newY);

  }

  // Placing the word on the world
  public WorldScene placeIWordOnWorld(WorldScene world) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return world.placeImageXY(
        new TextImage(this.word, 20, FontStyle.BOLD, Color.GREEN), 
        this.x, this.y);
  }

  @Override
  // Reduce the given letter from the string if the word is active
  public IWord reduceLetter(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (this.word.isEmpty() || letter.isEmpty()) {
      return this;
    } else {
      return new ActiveWord(word.substring(1), this.x, this.y);
    }
  }

  @Override
  // Check if the word is active 
  public boolean isActive() {
    //EVERYTHING IN THE CLASS TEMPLATE
    return true;
  }

  @Override
  // Check the first letter of the word, if it matches with the key pressed
  public boolean checkActiveLetter(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return (!this.word.isEmpty()
        && !letter.isEmpty()
        && (this.word.startsWith(letter)));
  }

}

// Represents an inactive word falling in the ZType game
class InactiveWord extends AWord {
  // the constructor
  InactiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  /*TEMPLATE
   * Fields:
   *  this.word ... String
   *  this.x ... integer
   *  this.y ... integer
   *  this.rand ... Random
   *
   * Method:
   *  this.reduceLetter(String) ... IWord
   *  this.placeIWordOnWorld(WorldScene) ... WorldScene
   *  this.moveWord() ... IWord
   *  this.isOut() ... boolean
   *  this.isStringEmpty() ... boolean
   *  this.checkActiveLetter(String) ... boolean
   *  this.isActive() ... boolean
   *  this.makeWordActive(String) ... IWord
   *  this.checkFirstLetter(String) ... boolean
   *  this.compareIWord(IWord) ... boolean
   *  this.compareYCoord(integer) ... boolean
   */

  // Move the Word down the y-axis
  public IWord moveWord() {
    //EVERYTHING IN THE CLASS TEMPLATE

    // Calculate the direction towards the target point
    // 200 is for the words to fly towards the middle
    // 500 is where the words will stop at the end
    int dx = 200 - this.x;
    int dy = 500 - this.y;

    // Calculate the magnitude of the movement
    double magnitude = Math.sqrt(dx * dx + dy * dy);

    // Calculate the unit vector
    int unitX = (int) Math.round(dx / magnitude);
    int unitY = (int) Math.round(dy / magnitude);

    // Move the dot towards the middle (target point)
    int newX = this.x + unitX * 5; // Adjust the magnitude as needed
    int newY = this.y + unitY * 5;

    return new InactiveWord(this.word, newX, newY);

  }

  // Placing the word on the world
  public WorldScene placeIWordOnWorld(WorldScene world) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return world.placeImageXY(
        new TextImage(this.word, 20, FontStyle.BOLD, Color.RED), 
        this.x, this.y);
  }

  @Override
  // Make the word Active if the first letter matches
  // this is where word is already active
  public IWord makeWordActive(String letter) {
    //EVERYTHING IN THE CLASS TEMPLATE
    if (this.word.startsWith(letter)) {
      return new ActiveWord(this.word, this.x, this.y).reduceLetter(letter);
    } else {
      return this;
    }
  }

  @Override
  // Check the first string of the Active word
  public boolean checkFirstLetter(String str) {
    //EVERYTHING IN THE CLASS TEMPLATE
    return this.word.startsWith(str);
  }

}

//////////////////////////////////////////////////////

//////////////////////////////////////////////////////

//////////////////////////////////////////////////////

// This represents the handle needed to restrict some components of the game
class Utils {
  // Form a word with the length of 6 (6 random letters)
  String randomWord(Random rand) {
    return randomStringGenerator(6, rand); 
  }

  // Form a word based on the length of the string
  // and randomized each letter based on the alphabet 
  // ASCII (0-122)
  String randomStringGenerator(int letters, Random rand) {
    int randomNum = rand.nextInt(123);

    if (letters <= 0) {
      return "";
    } else if ((letters <= 6) && (randomNum < 97)) {
      return randomStringGenerator(letters, rand);
    } else {
      return String.valueOf((char)randomNum) 
          + randomStringGenerator(letters - 1, rand);
    }
  }

  // Restrict the X-coordinates to only be within the game play scene
  public int restrictX() {
    Random rand = new Random();
    int randomNum = rand.nextInt(400);

    if (randomNum > 365 || randomNum < 35) {
      return restrictX();
    } else {
      return randomNum;
    }
  }
}



// Class for example of ZTypeGame
class ExamplesZTypeGame {
  //examples for active words
  IWord aLuffy = new ActiveWord("Luffy", 100, 100);
  IWord aZoro = new ActiveWord("Zoro", 150, 150);
  IWord aShip = new ActiveWord("ship", -50, 50);
  IWord aNami = new ActiveWord("Nami", 50, 50);
  IWord aKnife = new ActiveWord("knife", 75, -75);
  IWord aEmpty = new ActiveWord("", 1, 1);
  IWord aUmbrella = new ActiveWord("umbrella", 1, 1);
  IWord aSunnySky = new ActiveWord("sUnny sKy", 50, 50);
  IWord fun = new ActiveWord("Fun", 125, 100);
  IWord lies = new ActiveWord("Lies", 100, 100);
  IWord laugh = new ActiveWord("Laugh", 100, 90);

  //examples for inactive words
  IWord iaBigMom = new InactiveWord("Big Mom", 125, 100);
  IWord iaKaido = new InactiveWord("Kaido", 170, 150);
  IWord iaTequilla = new InactiveWord("tequilla", 50, 100);
  IWord iaSunAndCloud = new InactiveWord("sun and cloud", 75, 90);
  IWord iaBlackBeard = new InactiveWord("Black Beard", 0, 0);
  IWord iaEmpty = new InactiveWord("", 1, 1);

  //examples for empties
  ILoWord empty = new MtLoWord();
  ILoWord emptyActiveWords = new ConsLoWord(this.aEmpty, this.empty);
  ILoWord emptyInactiveWords = new ConsLoWord(this.iaEmpty, this.empty);

  //examples for list of active words only
  ILoWord captain = new ConsLoWord(this.aLuffy, this.empty);
  ILoWord flagCaptain = new ConsLoWord(this.aZoro, this.captain);
  ILoWord incharge = new ConsLoWord(this.aNami, this.flagCaptain);
  ILoWord sailingTheSea = new ConsLoWord(this.aShip, this.incharge);
  ILoWord strawHatCrew = new ConsLoWord(this.aKnife, this.sailingTheSea);
  ILoWord goodDayCaptain = new ConsLoWord(this.aSunnySky, this.flagCaptain);
  ILoWord weirdDay = new ConsLoWord(this.aUmbrella, this.goodDayCaptain);

  //examples for list of inactive words only
  ILoWord firstEnemy = new ConsLoWord(this.iaBigMom, this.empty);
  ILoWord kaidoAndBigMom = new ConsLoWord(this.iaKaido, this.firstEnemy);
  ILoWord friendsReunion = new ConsLoWord(this.iaTequilla, this.kaidoAndBigMom);
  ILoWord watchingTheSky = new ConsLoWord(this.iaSunAndCloud, this.friendsReunion);
  ILoWord battleOfTheEmperors = new ConsLoWord(this.iaBlackBeard, this.watchingTheSky);
  ILoWord sortedKaidoAndBigMom = new ConsLoWord(this.iaBigMom, 
      new ConsLoWord(this.iaKaido, this.empty));

  //examples for both active and inactive words in the list
  ILoWord captainFights = new ConsLoWord(this.aLuffy, this.kaidoAndBigMom);
  ILoWord zoroJoinsBattle = new ConsLoWord(this.aZoro, this.captainFights);
  ILoWord blackBeardJoinsBattle = new ConsLoWord(this.iaBlackBeard, this.zoroJoinsBattle);
  ILoWord everyoneFights = new ConsLoWord(this.aKnife, 
      new ConsLoWord(this.aNami, this.blackBeardJoinsBattle));
  ILoWord weirdFight = new ConsLoWord(this.iaBigMom, this.weirdDay);

  //examples for empty list in between the words within the list
  ILoWord theFightsOfTheEmptyStrings = 
      new ConsLoWord(this.aEmpty, 
          new ConsLoWord(this.aLuffy,
              new ConsLoWord(this.iaKaido, this.empty)));
  ILoWord aEmptyStringFights = new ConsLoWord(this.aEmpty, this.kaidoAndBigMom);
  ILoWord skyLooksBeautiful = new ConsLoWord(this.iaSunAndCloud, this.aEmptyStringFights);
  ILoWord iaEmptyStringFights = new ConsLoWord(this.iaEmpty, this.skyLooksBeautiful);
  ILoWord peace = new ConsLoWord(this.aSunnySky, 
      new ConsLoWord(this.iaSunAndCloud, this.iaEmptyStringFights));

  //sorted list of words with empty strings
  ILoWord sortedLoWordsWithEmpties = 
      new ConsLoWord(this.aEmpty,
          new ConsLoWord(this.aEmpty,
              new ConsLoWord(this.iaBigMom,
                  new ConsLoWord(this.iaKaido,
                      new ConsLoWord(this.iaSunAndCloud,
                          new ConsLoWord(this.iaSunAndCloud,
                              new ConsLoWord(this.aSunnySky, this.empty)))))));

  //shorter sorted lists of words with empty words
  ILoWord shortSortedLoWordWithEmpties = 
      new ConsLoWord(this.aEmpty,
          new ConsLoWord(this.iaEmpty,
              new ConsLoWord(this.iaBigMom,
                  new ConsLoWord(this.iaKaido,
                      new ConsLoWord(this.aSunnySky, this.empty)))));

  //shorter sorted list of words
  ILoWord shortSortedLoWord =
      new ConsLoWord(this.iaBigMom,
          new ConsLoWord(this.iaKaido,
              new ConsLoWord(this.aLuffy, this.empty)));


  //shorter sorted list of active words
  ILoWord shortSortedActiveLoWord =
      new ConsLoWord(this.aLuffy,
          new ConsLoWord(this.aNami, this.empty));

  //shorter sorted list of active words
  ILoWord shortSortedActiveLoWordWithEmpties =
      new ConsLoWord(this.aLuffy,
          new ConsLoWord(this.aEmpty,
              new ConsLoWord(this.aNami,
                  new ConsLoWord(this.aEmpty, this.empty))));

  //sorted list of active words with empties
  ILoWord sortedActiveWords = 
      new ConsLoWord(this.aKnife,
          new ConsLoWord(this.aLuffy,
              new ConsLoWord(this.aNami,
                  new ConsLoWord(this.aShip,
                      new ConsLoWord(this.aZoro, this.empty)))));

  //sorted list of inactive words with empty
  ILoWord shortSortedInactiveWordsWithEmpties =
      new ConsLoWord(this.iaKaido,
          new ConsLoWord(this.iaTequilla, 
              new ConsLoWord(this.aEmpty,
                  new ConsLoWord(this.iaEmpty, this.empty))));

  //sorted list of inactive words
  ILoWord shortSortedInactiveWords =
      new ConsLoWord(this.iaKaido,
          new ConsLoWord(this.iaTequilla, this.empty));

  //sorted list of inactive words
  ILoWord sortedInactiveWords = 
      new ConsLoWord(this.iaBigMom,
          new ConsLoWord(this.iaBlackBeard,
              new ConsLoWord(this.iaKaido,
                  new ConsLoWord(this.iaSunAndCloud,
                      new ConsLoWord(this.iaTequilla, this.empty)))));

  //sorted list of words without empty strings
  ILoWord sortedLoWords = 
      new ConsLoWord(this.iaBigMom,
          new ConsLoWord(this.iaBlackBeard,
              new ConsLoWord(this.iaKaido,
                  new ConsLoWord(this.aKnife,
                      new ConsLoWord(this.aLuffy, 
                          new ConsLoWord(this.aNami,
                              new ConsLoWord(this.aZoro, this.empty)))))));  

  //test for sort
  boolean testSort(Tester t) {
    return 
        //sort an empty list
        t.checkExpect(this.empty.sort(), this.empty)
        //sort a list with empty strings component
        //with duplicated first letter
        //and with a duplicated word 
        && t.checkExpect(this.peace.sort(), 
            new ConsLoWord(this.iaKaido,
                new ConsLoWord(this.iaBigMom,
                    new ConsLoWord(this.iaSunAndCloud,
                        new ConsLoWord(this.iaSunAndCloud,
                            new ConsLoWord(this.aSunnySky,
                                new ConsLoWord(this.aEmpty,
                                    new ConsLoWord(this.iaEmpty, this.empty))))))))
        //sort a list with 1 active component
        && t.checkExpect(this.captain.sort(), this.captain)
        //sort a list with 1 inactive component
        && t.checkExpect(this.firstEnemy.sort(), this.firstEnemy)
        //sort a list with multiple only active components
        && t.checkExpect(this.strawHatCrew.sort(),
            new ConsLoWord(this.aZoro,
                new ConsLoWord(this.aLuffy,
                    new ConsLoWord(this.aNami,
                        new ConsLoWord(this.aShip,
                            new ConsLoWord(this.aKnife, this.empty))))))
        //sort a list with multiple only inactive components
        && t.checkExpect(this.battleOfTheEmperors.sort(), 
            new ConsLoWord(this.iaKaido,
                new ConsLoWord(this.iaBigMom,
                    new ConsLoWord(this.iaTequilla,
                        new ConsLoWord(this.iaSunAndCloud,
                            new ConsLoWord(this.iaBlackBeard, this.empty))))))
        //sort a list with both active and inactive components without empty strings
        //and a list with same first letter
        && t.checkExpect(this.everyoneFights.sort(), 
            new ConsLoWord(this.iaKaido,
                new ConsLoWord(this.aZoro,
                    new ConsLoWord(this.iaBigMom,
                        new ConsLoWord(this.aLuffy,
                            new ConsLoWord(this.aNami, 
                                new ConsLoWord(this.iaBlackBeard,
                                    new ConsLoWord(this.aKnife, this.empty))))))));
  }

  //test for compareAndSortWord
  boolean testCompareAndSortWord(Tester t) {
    return 
        //empty list on empty word
        t.checkExpect(this.empty.compareAndSortWord(aEmpty),
            new ConsLoWord(this.aEmpty, this.empty))
        //empty list on word
        && t.checkExpect(this.empty.compareAndSortWord(aLuffy), 
            new ConsLoWord(this.aLuffy, this.empty))
        //list and a word
        && t.checkExpect(this.sortedKaidoAndBigMom.compareAndSortWord(aLuffy), 
            new ConsLoWord(this.iaBigMom,
                new ConsLoWord(this.iaKaido,
                    new ConsLoWord(this.aLuffy, this.empty))));
  }

  //test for filterOutEmpties
  boolean testFilterOutEmpties(Tester t) {
    return 
        //filter a list of active words with no empties
        t.checkExpect(this.strawHatCrew.filterOutEmpties(), this.strawHatCrew)
        //filter a list of inactive words with no empties
        && t.checkExpect(this.battleOfTheEmperors.filterOutEmpties(), this.battleOfTheEmperors)
        //filter a list of both inactive and active words with no empties
        && t.checkExpect(this.everyoneFights.filterOutEmpties(), this.everyoneFights)
        //filter an empty list
        && t.checkExpect(this.empty.filterOutEmpties(), this.empty)
        //filter a list of active words with an active empty string word
        && t.checkExpect(this.shortSortedActiveLoWordWithEmpties.filterOutEmpties(), 
            this.shortSortedActiveLoWord)
        //filter a list of inactive words with an inactive empty string word
        && t.checkExpect(this.shortSortedInactiveWordsWithEmpties.filterOutEmpties(),
            this.shortSortedInactiveWords)
        //filter a list of inactive words and inactive words 
        //with both inactive and active empty string word
        && t.checkExpect(this.sortedLoWordsWithEmpties.filterOutEmpties(),
            new ConsLoWord(this.iaBigMom,
                new ConsLoWord(this.iaKaido,
                    new ConsLoWord(this.iaSunAndCloud,
                        new ConsLoWord(this.iaSunAndCloud,
                            new ConsLoWord(this.aSunnySky, this.empty))))));

  }

  //test for checkAndReduce
  boolean testCheckAndReduce(Tester t) {
    return 
        //reduce word, given an empty string
        t.checkExpect(this.peace.checkAndReduce(""), this.peace)
        //reduce a letter from an empty list
        && t.checkExpect(this.empty.checkAndReduce("c"), this.empty)
        //reduce an empty list from an empty string
        && t.checkExpect(this.empty.checkAndReduce(""), this.empty)
        //reduce a letter from a list with both capitalized and not capitalized 
        //of that letter
        && t.checkExpect(this.weirdFight.checkAndReduce("u"), 
            new ConsLoWord(this.iaBigMom,
                new ConsLoWord(new ActiveWord("mbrella", 1, 1),
                    new ConsLoWord(this.aSunnySky,
                        new ConsLoWord(this.aZoro,
                            new ConsLoWord(this.aLuffy, this.empty))))))
        //reduce a capitalized letter from a list with both capitalized and not capitalized 
        //of that letter
        && t.checkExpect(this.weirdFight.checkAndReduce("U"), this.weirdFight)
        //reduce a list of only active words
        && t.checkExpect(this.weirdDay.checkAndReduce("u"),
            new ConsLoWord(new ActiveWord("mbrella", 1, 1),
                new ConsLoWord(this.aSunnySky,
                    new ConsLoWord(this.aZoro,
                        new ConsLoWord(this.aLuffy, this.empty)))))
        //reduce a list of only inactive words
        && t.checkExpect(this.battleOfTheEmperors.checkAndReduce("a"), this.battleOfTheEmperors)
        //reduce a list of both inactive and active words
        //reduce a list when no letter matches with the given one
        && t.checkExpect(this.everyoneFights.checkAndReduce("q"), this.everyoneFights)
        //reduce a list given empty string
        && t.checkExpect(this.weirdDay.checkAndReduce(" "), this.weirdDay)
        //reduce a list that contains empty strings
        && t.checkExpect(this.shortSortedLoWordWithEmpties.checkAndReduce("a"), 
            this.shortSortedLoWordWithEmpties);
  }

  //test for draw
  boolean testDraw(Tester t) {
    // WorldCanvas c1 = new WorldCanvas(400, 600);
    WorldScene s1 = new WorldScene(400, 600);
    return 
        //test if draw() method draws an empty list
        t.checkExpect(this.empty.draw(s1), this.empty.draw(s1))
        &&
        //test if draw() method draws a list with 1 active word 
        t.checkExpect(this.captain.draw(s1), this.captain.draw(s1))
        &&
        //test if draw() method draws a list with 1 inactive word
        t.checkExpect(this.firstEnemy.draw(s1), this.firstEnemy.draw(s1))
        &&
        //test if draw() method draws a list with all active words
        t.checkExpect(this.strawHatCrew.draw(s1), this.strawHatCrew.draw(s1))
        &&
        //test if draw() method draws a list with all inactive words
        t.checkExpect(this.battleOfTheEmperors.draw(s1), this.battleOfTheEmperors.draw(s1))
        &&
        //test if draw() method draws a list with mix of active & inactive words
        //with empty strings words
        t.checkExpect(this.peace.draw(s1), this.peace.draw(s1));

  }

  //test for reduceLetter
  boolean testReduceLetter(Tester t) {
    return 
        //reduce an active word
        t.checkExpect(this.aLuffy.reduceLetter("a"), new ActiveWord("uffy", 100, 100))
        //reduce an inactive
        && t.checkExpect(this.iaBigMom.reduceLetter("L"), this.iaBigMom)
        //reduce a empty active word
        && t.checkExpect(this.aEmpty.reduceLetter("l"), this.aEmpty)
        //reduce a empty inactive word
        && t.checkExpect(this.iaEmpty.reduceLetter("l"), this.iaEmpty)
        //reduce a word given a empty string
        && t.checkExpect(this.aLuffy.reduceLetter(""), this.aLuffy);
  }

  boolean testPlaceIWordOnWorld(Tester t) {
    // WorldCanvas c2 = new WorldCanvas(400, 600);
    WorldScene c2 = new WorldScene(400, 600);
    return 
        //draws an active empty word
        t.checkExpect(this.aEmpty.placeIWordOnWorld(c2), this.aEmpty.placeIWordOnWorld(c2))
        &&
        //draws a active word 
        t.checkExpect(this.aLuffy.placeIWordOnWorld(c2), this.aLuffy.placeIWordOnWorld(c2))
        &&
        //draws a active word 
        t.checkExpect(this.iaBigMom.placeIWordOnWorld(c2), this.iaBigMom.placeIWordOnWorld(c2))
        &&
        //draws a empty inactive word 
        t.checkExpect(this.iaEmpty.placeIWordOnWorld(c2), this.iaEmpty.placeIWordOnWorld(c2));
  }

  // Test randomWord
  boolean testRandomWord(Tester t) {
    Utils u = new Utils();
    Random random1 = new Random(911);
    String randomWord1 = u.randomWord(random1);
    String expectedWord1 = "fmjxcj";

    Random random2 = new Random(-8);
    String randomWord2 = u.randomWord(random2);
    String expectedWord2 = "cfclth";

    Random random3 = new Random(0);
    String randomWord3 = u.randomWord(random3);
    String expectedWord3 = "lqbnig";
    return 
        //check for a positive large number > 122
        t.checkExpect(randomWord1, expectedWord1)
        &&
        //check for a negative random
        t.checkExpect(randomWord2, expectedWord2)
        && 
        //check for a 0 random, < 97
        t.checkExpect(randomWord3, expectedWord3);
  }

  // Test randomStringGenerator
  boolean testRandomStringGenerator(Tester t) {
    Utils u = new Utils();
    Random random1 = new Random(911);
    String randomWord1 = u.randomStringGenerator(-2, random1);
    String expectedWord1 = "";

    Random random2 = new Random(-8);
    String randomWord2 = u.randomStringGenerator(0, random2);
    String expectedWord2 = "";

    Random random3 = new Random(0);
    String randomWord3 = u.randomStringGenerator(2, random3);
    String expectedWord3 = "lq";
    return 
        //check for a positive random and negative letters
        t.checkExpect(randomWord1, expectedWord1)
        //check for a 0 random, and positive letters
        && t.checkExpect(randomWord3, expectedWord3)
        //check for a negative random, and a 0 letters
        && t.checkExpect(randomWord2, expectedWord2);
  }

  //test for moveWord within ILoWord
  boolean testMoveWordILoWord(Tester t) {
    return t.checkExpect(this.emptyActiveWords.moveWord(), 
        new ConsLoWord(new ActiveWord("", 1, 6), new MtLoWord()))
        && t.checkExpect(this.captain.moveWord(), 
            new ConsLoWord(new ActiveWord("Luffy", 100, 105), new MtLoWord()))
        && t.checkExpect(this.kaidoAndBigMom.moveWord(), 
            new ConsLoWord(new InactiveWord("Kaido", 170, 155), 
                new ConsLoWord(new InactiveWord("Big Mom", 125, 105), new MtLoWord())))
        && t.checkExpect(this.flagCaptain.moveWord(), 
            new ConsLoWord(new ActiveWord("Zoro", 150, 155), new ConsLoWord(
                new ActiveWord("Luffy", 100, 105), new MtLoWord())))
        && t.checkExpect(this.zoroJoinsBattle.moveWord(), new ConsLoWord(
            new ActiveWord("Zoro", 150, 155), new ConsLoWord(new ActiveWord("Luffy", 100, 105), 
                new ConsLoWord(new InactiveWord("Kaido", 170, 155), new ConsLoWord(
                    new InactiveWord("Big Mom", 125, 105), new MtLoWord())))))
        && t.checkExpect(this.theFightsOfTheEmptyStrings.moveWord(), 
            new ConsLoWord(new ActiveWord("", 1, 6), new ConsLoWord(
                new ActiveWord("Luffy", 100, 105), new ConsLoWord(
                    new InactiveWord("Kaido", 170, 155), new MtLoWord()))));
  }

  // test for moveWord within IWord
  boolean testMoveWordIWord(Tester t) {
    return t.checkExpect(this.aLuffy.moveWord(), new ActiveWord("Luffy", 100, 105))
        && t.checkExpect(this.aNami.moveWord(), new ActiveWord("Nami", 50, 55))
        && t.checkExpect(this.aEmpty.moveWord(), new ActiveWord("", 1, 6))
        && t.checkExpect(this.iaEmpty.moveWord(), new InactiveWord("", 1, 6))
        && t.checkExpect(this.iaBlackBeard.moveWord(), new InactiveWord("Black Beard", 0, 5))
        && t.checkExpect(this.iaSunAndCloud.moveWord(), new InactiveWord("sun and cloud", 75, 95));
  }

  //test for isOut within ILoWord
  boolean testIsOutILoWord(Tester t) {
    return t.checkExpect(this.emptyActiveWords.isOut(), false)
        && t.checkExpect(this.captain.isOut(), false)
        && t.checkExpect(this.kaidoAndBigMom.isOut(), false)
        && t.checkExpect(this.flagCaptain.isOut(), false)
        && t.checkExpect(this.zoroJoinsBattle.isOut(), false)
        && t.checkExpect(this.theFightsOfTheEmptyStrings.isOut(), false);
  }


  //test for isOut within IWord
  boolean testIsOutIWord(Tester t) {
    return t.checkExpect(this.aLuffy.isOut(), false)
        && t.checkExpect(this.aNami.isOut(), false)
        && t.checkExpect(this.aEmpty.isOut(), false)
        && t.checkExpect(this.iaEmpty.isOut(), false)
        && t.checkExpect(this.iaBlackBeard.isOut(), false)
        && t.checkExpect(this.iaSunAndCloud.isOut(), false);
  }

  //test for computeScore
  boolean testComputeScore(Tester t) {
    return t.checkExpect(this.kaidoAndBigMom.computeScore(), 0)
        && t.checkExpect(this.strawHatCrew.computeScore(), 0)
        && t.checkExpect(this.emptyActiveWords.computeScore(), 50)
        && t.checkExpect(this.captain.computeScore(), 0)
        && t.checkExpect(this.flagCaptain.computeScore(), 0)
        && t.checkExpect(this.zoroJoinsBattle.computeScore(), 0)
        && t.checkExpect(this.theFightsOfTheEmptyStrings.computeScore(), 50);
  }

  //test for anyActive
  boolean testAnyActive(Tester t) {
    return t.checkExpect(this.kaidoAndBigMom.anyActive(), false)
        && t.checkExpect(this.strawHatCrew.anyActive(), true)
        && t.checkExpect(this.emptyActiveWords.anyActive(), true)
        && t.checkExpect(this.captain.anyActive(), true)
        && t.checkExpect(this.flagCaptain.anyActive(), true)
        && t.checkExpect(this.zoroJoinsBattle.anyActive(), true)
        && t.checkExpect(this.theFightsOfTheEmptyStrings.anyActive(), true);
  }

  //test for checkFirst within ILoWord
  boolean testCheckFirstILoWord(Tester t) {
    return t.checkExpect(this.kaidoAndBigMom.checkFirst(""), true)
        && t.checkExpect(this.strawHatCrew.checkFirst("e"), false)
        && t.checkExpect(this.emptyActiveWords.checkFirst("j"), false)
        && t.checkExpect(this.captain.checkFirst("m"), false)
        && t.checkExpect(this.flagCaptain.checkFirst(" "), false)
        && t.checkExpect(this.zoroJoinsBattle.checkFirst(";"), false)
        && t.checkExpect(this.theFightsOfTheEmptyStrings.checkFirst("w"), false);
  }

  //test for makeWordActive within ILoWord
  boolean testMakeWordActiveILoWord(Tester t) {
    return t.checkExpect(this.kaidoAndBigMom.makeWordActive(""), new ConsLoWord(
        new ActiveWord("Kaido", 170, 150), new ConsLoWord(this.iaBigMom, this.empty)))
        && t.checkExpect(this.emptyActiveWords.makeWordActive("j"), this.emptyActiveWords)
        && t.checkExpect(this.captain.makeWordActive("m"), this.captain)
        && t.checkExpect(this.flagCaptain.makeWordActive(" "), this.flagCaptain)
        && t.checkExpect(this.zoroJoinsBattle.makeWordActive("w"), new ConsLoWord(
            new InactiveWord("Kaido", 170, 150), new ConsLoWord(new ActiveWord("Zoro", 150, 150), 
                new ConsLoWord(new InactiveWord("Big Mom", 125, 100), new ConsLoWord(
                    new ActiveWord("Luffy", 100, 100), new MtLoWord())))))
        && t.checkExpect(this.strawHatCrew.makeWordActive("e"), new ConsLoWord(
            new ActiveWord("Zoro", 150, 150), new ConsLoWord(new ActiveWord("Luffy", 100, 100), 
                new ConsLoWord(new ActiveWord("Nami", 50, 50), new ConsLoWord(
                    new ActiveWord("ship", -50, 50), 
                    new ConsLoWord(new ActiveWord("knife", 75, -75), 
                        new MtLoWord()))))));
  }

  //test for makeWordActive within IWord
  boolean testMakeWordActiveIWord(Tester t) {
    return t.checkExpect(this.kaidoAndBigMom.checkFirst(""), true)
        && t.checkExpect(this.strawHatCrew.checkFirst("e"), false)
        && t.checkExpect(this.emptyActiveWords.checkFirst("j"), false)
        && t.checkExpect(this.captain.checkFirst("m"), false)
        && t.checkExpect(this.flagCaptain.checkFirst(" "), false)
        && t.checkExpect(this.zoroJoinsBattle.checkFirst(";"), false)
        && t.checkExpect(this.theFightsOfTheEmptyStrings.checkFirst("W"), false);
  }

  //test for makeFirstActive
  boolean testMakeFirstActive(Tester t) {
    return t.checkExpect(this.kaidoAndBigMom.makeFirstActive(""), new ConsLoWord(
        new ActiveWord("Kaido", 170, 150), new ConsLoWord(
            new InactiveWord("Big Mom", 125, 100), new MtLoWord())))
        && t.checkExpect(this.strawHatCrew.makeFirstActive("E"), new ConsLoWord(
            this.aKnife, new ConsLoWord(this.aShip, new ConsLoWord(
                this.aNami, new ConsLoWord(this.aZoro, new ConsLoWord(
                    this.aLuffy, new MtLoWord()))))))
        && t.checkExpect(this.emptyActiveWords.makeFirstActive("j"), new ConsLoWord(
            this.aEmpty, new MtLoWord()))
        && t.checkExpect(this.captain.makeFirstActive("M"), new ConsLoWord(
            this.aLuffy, new MtLoWord()))
        && t.checkExpect(this.flagCaptain.makeFirstActive(" "), new ConsLoWord(
            new ActiveWord("Zoro", 150, 150), new ConsLoWord(
                new ActiveWord("Luffy", 100, 100), new MtLoWord())))
        && t.checkExpect(this.zoroJoinsBattle.makeFirstActive(";"), new ConsLoWord(
            new ActiveWord("Zoro", 150, 150), new ConsLoWord(
                new ActiveWord("Luffy", 100, 100),  new ConsLoWord(
                    new InactiveWord("Kaido", 170, 150), new ConsLoWord(
                        new InactiveWord("Big Mom", 125, 100), new MtLoWord())))))
        && t.checkExpect(this.theFightsOfTheEmptyStrings.makeFirstActive("w"), 
            this.theFightsOfTheEmptyStrings);
  }

  //test for findActive
  boolean testFindActive(Tester t) {
    return t.checkExpect(this.kaidoAndBigMom.findActive(""), new ConsLoWord(
        new InactiveWord("Kaido", 170, 150), new ConsLoWord(
            new ActiveWord("Big Mom", 125, 100), new MtLoWord())))
        && t.checkExpect(this.strawHatCrew.findActive("EE"), new ConsLoWord(
            new ActiveWord("knife", 75, -75), new ConsLoWord(new ActiveWord("Zoro", 150, 150), 
                new ConsLoWord(new ActiveWord("Luffy", 100, 100),
                    new ConsLoWord(new ActiveWord("Nami", 50, 50),new ConsLoWord(
                        new ActiveWord("ship", -50, 50), new MtLoWord()))))))
        && t.checkExpect(this.emptyActiveWords.findActive("J"), this.emptyActiveWords)
        && t.checkExpect(this.captain.findActive("m"), new ConsLoWord(this.aLuffy, new MtLoWord()))
        && t.checkExpect(this.flagCaptain.findActive(" "), new ConsLoWord(
            new ActiveWord("Zoro", 150, 150), new ConsLoWord(
                new ActiveWord("Luffy", 100, 100), new MtLoWord())) )
        && t.checkExpect(this.zoroJoinsBattle.findActive(";"), new ConsLoWord(
            new ActiveWord("Zoro", 150, 150), new ConsLoWord(
                new InactiveWord("Kaido", 170, 150), new ConsLoWord(
                    new ActiveWord("Luffy", 100, 100),  new ConsLoWord(
                        new InactiveWord("Big Mom", 125, 100), new MtLoWord())))))
        && t.checkExpect(this.theFightsOfTheEmptyStrings.findActive("w"), new ConsLoWord(
            this.aEmpty, new ConsLoWord(this.iaKaido, new ConsLoWord(this.aLuffy, this.empty))));
  }

  //test for isStringEmpty
  boolean testIsStringEmpty(Tester t) {
    return t.checkExpect(this.aLuffy.isStringEmpty(), false)
        && t.checkExpect(this.aNami.isStringEmpty(), false)
        && t.checkExpect(this.aEmpty.isStringEmpty(), true)
        && t.checkExpect(this.iaEmpty.isStringEmpty(), true)
        && t.checkExpect(this.iaBlackBeard.isStringEmpty(), false)
        && t.checkExpect(this.iaSunAndCloud.isStringEmpty(), false);
  }

  //test for checkActiveLetter
  boolean testCheckActiveLetter(Tester t) {
    return t.checkExpect(this.aLuffy.checkActiveLetter("L"), true)
        && t.checkExpect(this.aNami.checkActiveLetter(""), false)
        && t.checkExpect(this.aEmpty.checkActiveLetter(";"), false)
        && t.checkExpect(this.iaEmpty.checkActiveLetter(""), false)
        && t.checkExpect(this.iaBlackBeard.checkActiveLetter("B"), false)
        && t.checkExpect(this.iaSunAndCloud.checkActiveLetter("S"), false);
  }

  //test for isActive
  boolean testIsActive(Tester t) {
    return 
        //test an active word
        t.checkExpect(this.aLuffy.isActive(), true)
        //test an inactive word
        && t.checkExpect(this.iaBigMom.isActive(), false)
        //test an active empty word
        && t.checkExpect(this.aEmpty.isActive(), true)
        //test an inactive empty word
        && t.checkExpect(this.iaEmpty.isActive(), false)
        ;
  }

  //test for checkFirstLetter within IWord
  boolean testCheckFirstLetter(Tester t) {
    return 
        //test an active word
        t.checkExpect(this.aLuffy.checkFirstLetter("L"), true)
        //test an inactive word
        && t.checkExpect(this.iaBigMom.checkFirstLetter("B"), true)
        //test an inactive word with empty string given
        && t.checkExpect(this.iaBigMom.checkFirstLetter(""), true)
        //test an active word with empty string given
        && t.checkExpect(this.aLuffy.checkFirstLetter(""), false)
        //test an active empty word
        && t.checkExpect(this.aEmpty.checkFirstLetter("v"), false)
        //test an inactive empty word
        && t.checkExpect(this.iaEmpty.checkFirstLetter("d"), false)
        ;
  }

  //test for compareIWord
  boolean testCompareIWord(Tester t) {
    return 
        //test an active word with an active word at the same location
        t.checkExpect(this.aLuffy.compareIWord(this.aLuffy), false)
        //test an inactive word and an inactive word at the same location
        && t.checkExpect(this.iaBigMom.compareIWord(this.iaBigMom), false)
        //test an inactive word with an active word at different locations
        && t.checkExpect(this.iaBigMom.compareIWord(this.aZoro), true)
        //test an active word with an inactive word at the same location
        && t.checkExpect(this.aLuffy.compareIWord(this.fun), false)
        //test an active empty word with an inactive word at different location
        && t.checkExpect(this.aEmpty.compareIWord(this.iaEmpty), false)
        //test an inactive empty word and an inactive empty word at different locations
        && t.checkExpect(this.iaEmpty.compareIWord(this.iaEmpty), false)
        ;
  }

  //test for compareYCoord 
  boolean testCompareYCoord(Tester t) {
    return 
        //test an active word with a lower location
        t.checkExpect(this.aLuffy.compareYCoord(0), true)
        //test an inactive word with a lower location
        && t.checkExpect(this.iaBigMom.compareYCoord(0), true)
        //test an active word with a equal location
        && t.checkExpect(this.aLuffy.compareYCoord(100), false)
        //test an active word with a higher location
        && t.checkExpect(this.aLuffy.compareYCoord(150), false)
        //test an inactive word with a higher location
        && t.checkExpect(this.iaBigMom.compareYCoord(150), false)
        ;
  }

  //empty world
  ZTypeWorld emptyWorld = new ZTypeWorld(new MtLoWord(), 2);
  //world with both active and inactive words
  ZTypeWorld mixWorld = new ZTypeWorld(this.captainFights, 2);
  //world with only active words
  ZTypeWorld activeWorld = new ZTypeWorld(this.flagCaptain, 2);
  //world with multiple active words with the same beginning letter
  ZTypeWorld multipleActiveWorld = 
      new ZTypeWorld(new ConsLoWord(this.lies,
          new ConsLoWord(this.aLuffy, this.empty)), 2);
  //world with multiple active words with the same beginning letter
  ZTypeWorld multipleActiveWorldWithLaugh = 
      new ZTypeWorld(new ConsLoWord(this.lies,
          new ConsLoWord(this.laugh, this.empty)), 2);
  //world with only inactive words
  ZTypeWorld inactiveWorld = new ZTypeWorld(this.kaidoAndBigMom, 2);
  //world with active, inactive, and empty words
  ZTypeWorld peaceWorld = new ZTypeWorld(this.theFightsOfTheEmptyStrings, 2);
  //world with negative coordinates
  ZTypeWorld piratesWorld = new ZTypeWorld(this.sailingTheSea, 2);



  //test for makeScene
  boolean testMakeScene(Tester t) {
    OverlayOffsetImage rocket1 = new OverlayOffsetImage(new TriangleImage(
        new Posn(25, 0), new Posn(0, 25), new Posn(50, 25), "solid", Color.RED), 
        0, 30, 
        (new EllipseImage(30, 60, OutlineMode.SOLID, Color.LIGHT_GRAY)));

    OverlayOffsetImage rocket2 = new OverlayOffsetImage(
        new CircleImage(10, "solid", Color.BLUE), 0, 0, 
        rocket1);

    OverlayOffsetImage rocket3 = new OverlayOffsetImage(new TriangleImage(
        new Posn(15, 0), new Posn(0, 15), new Posn(15, 15), 
        "solid", Color.RED), 15, -30, rocket2);

    OverlayOffsetImage rocket4 = new OverlayOffsetImage(new TriangleImage(
        new Posn(-15, 0), new Posn(-15, 15), new Posn(0, 15), 
        "solid", Color.RED), -15, -30, rocket3);

    return
        // no words on world
        t.checkExpect(emptyWorld.makeScene(),
            new WorldScene(400, 600).placeImageXY(rocket4, 200, 520))
        // a world with only active words
        && t.checkExpect(activeWorld.makeScene(), 
            new WorldScene(400, 600)
            .placeImageXY(rocket4, 200, 520)
            .placeImageXY(new TextImage("Luffy",  20.0, FontStyle.BOLD, Color.GREEN), 100, 100)
            .placeImageXY(new TextImage("Zoro", 20.0, FontStyle.BOLD, Color.GREEN), 150, 150))
        // a world with only inactive words
        && t.checkExpect(inactiveWorld.makeScene(), new WorldScene(400, 600)
            .placeImageXY(new TextImage("Kaido", 20.0, FontStyle.BOLD, Color.RED), 170, 150)
            .placeImageXY(new TextImage("Big Mom", 20.0, FontStyle.BOLD, Color.RED), 125, 100)
            .placeImageXY(rocket4, 200, 520))
        // a world with both active and inactive words 
        && t.checkExpect(mixWorld.makeScene(), new WorldScene(400, 600)
            .placeImageXY(new TextImage("Luffy", 20.0, FontStyle.BOLD, Color.GREEN), 100, 100)
            .placeImageXY(new TextImage("Kaido", 20.0, FontStyle.BOLD, Color.RED), 170, 150)
            .placeImageXY(new TextImage("Big Mom", 20.0, FontStyle.BOLD, Color.RED), 125, 100)
            .placeImageXY(rocket4, 200, 520))
        // a world with inactive, active, and empty word
        && t.checkExpect(peaceWorld.makeScene(), new WorldScene(400, 600)
            .placeImageXY(new TextImage("", 20.0, FontStyle.BOLD, Color.RED), 1, 1)
            .placeImageXY(new TextImage("Luffy", 20.0, FontStyle.BOLD, Color.GREEN), 100, 100)
            .placeImageXY(new TextImage("Kaido", 20.0, FontStyle.BOLD, Color.RED), 170, 150)
            .placeImageXY(rocket4, 200, 520))
        // a world with word that falls out of the game world (negative coordinates)
        && t.checkExpect(piratesWorld.makeScene(), new WorldScene(400, 600)
            .placeImageXY(new TextImage("Ship", 20.0, FontStyle.BOLD, Color.GREEN), -50, 50)
            .placeImageXY(new TextImage("Nami", 20.0, FontStyle.BOLD, Color.GREEN), 50, 50)
            .placeImageXY(new TextImage("Zoro", 20.0, FontStyle.BOLD, Color.GREEN), 150, 150)
            .placeImageXY(new TextImage("Luffy", 20.0, FontStyle.BOLD, Color.GREEN), 100, 100)
            .placeImageXY(rocket4, 200, 520))
        ;
  }

  //test for onKeyEvent
  boolean testOnKeyEvent(Tester t) {
    return
        //test an empty world
        t.checkExpect(this.emptyWorld.onKeyEvent("a"), this.emptyWorld)
        //test a world with only active words, with the matching letter
        //reduce the word
        && t.checkExpect(this.activeWorld.onKeyEvent("L"),
            new ZTypeWorld(
                new ConsLoWord(this.aZoro, 
                    new ConsLoWord(new ActiveWord("uffy", 100, 100), this.empty)), 2))
        //test a world with only active words, withOUT the matching letter
        //reduce the word
        && t.checkExpect(this.activeWorld.onKeyEvent("H"), this.activeWorld)
        //test a world with only inactive words, with the matching letter
        //inactive word turned into active and reduce
        && t.checkExpect(this.inactiveWorld.onKeyEvent("K"), 
            new ZTypeWorld(
                new ConsLoWord(new ActiveWord("aido", 170, 150),
                    new ConsLoWord(this.iaBigMom, this.empty)), 2))
        //test a world with only inactive words, withOUT the matching letter
        //inactive word turned into active and reduce
        && t.checkExpect(this.inactiveWorld.onKeyEvent("L"), this.inactiveWorld)
        //test a world with both active and inactive words
        //that the given letter matches an inactive word
        //but DOES NOT reduce because an active word is currently in place
        && t.checkExpect(this.mixWorld.onKeyEvent("B"), 
            new ZTypeWorld(
                new ConsLoWord(this.aLuffy,
                    new ConsLoWord(this.iaKaido,
                        new ConsLoWord(this.iaBigMom, this.empty))), 2))
        //test a world with both active and inactive words
        //that the given letter matches an active word
        //DOES reduce because an active word is currently in place
        && t.checkExpect(this.mixWorld.onKeyEvent("L"), 
            new ZTypeWorld(
                new ConsLoWord(new ActiveWord("uffy", 100, 100),
                    new ConsLoWord(this.iaKaido,
                        new ConsLoWord(this.iaBigMom, this.empty))), 2))
        //test a world with active, inactive, and empty words 
        //that letter matches active word
        //empty word will be cut
        //active word will be reduced
        && t.checkExpect(this.peaceWorld.onKeyEvent("L"), 
            new ZTypeWorld(
                new ConsLoWord(new ActiveWord("uffy", 100, 100),
                    new ConsLoWord(this.iaKaido, this.empty)), 2))
        //test a world with active, inactive, and empty words 
        //that letter matches the inactive
        //empty word will be cut
        //active word will be reduced
        && t.checkExpect(this.peaceWorld.onKeyEvent("K"), 
            new ZTypeWorld(
                new ConsLoWord(this.aLuffy,
                    new ConsLoWord(this.iaKaido, this.empty)), 2))
        //test a world with active, inactive, and empty words taken in an empty string
        && t.checkExpect(this.peaceWorld.onKeyEvent(""), 
            new ZTypeWorld(
                new ConsLoWord(this.aLuffy,
                    new ConsLoWord(this.iaKaido, this.empty)), 2))
        //test a world with multiple active words, with the same given letter pushed
        //they are also at the same location
        && t.checkExpect(this.multipleActiveWorld.onKeyEvent("L"), 
            new ZTypeWorld(
                new ConsLoWord(new ActiveWord("ies", 100, 100),
                    new ConsLoWord(new ActiveWord("uffy", 100, 100), this.empty)), 2))
        //test a world with multiple active words, with the same given letter pushed
        //laugh is at a higher location
        && t.checkExpect(this.multipleActiveWorldWithLaugh.onKeyEvent("L"), 
            new ZTypeWorld(
                new ConsLoWord(new ActiveWord("ies", 100, 100),
                    new ConsLoWord(new ActiveWord("augh", 100, 90), this.empty)), 2))

        ;
  }

  //test for worldEnds AND makeAFinalScene
  boolean testWorldEndsAndMakeAFinalScene(Tester t) {
    TextImage gameOver = new TextImage("GAME OVER", 400 / 10, FontStyle.BOLD, Color.RED);
    OverlayOffsetImage rocket1 = new OverlayOffsetImage(new TriangleImage(
        new Posn(25, 0), new Posn(0, 25), new Posn(50, 25), "solid", Color.RED), 
        0, 30, 
        (new EllipseImage(30, 60, OutlineMode.SOLID, Color.LIGHT_GRAY)));

    OverlayOffsetImage rocket2 = new OverlayOffsetImage(
        new CircleImage(10, "solid", Color.BLUE), 0, 0, 
        rocket1);

    OverlayOffsetImage rocket3 = new OverlayOffsetImage(new TriangleImage(
        new Posn(15, 0), new Posn(0, 15), new Posn(15, 15), 
        "solid", Color.RED), 15, -30, rocket2);

    OverlayOffsetImage rocket4 = new OverlayOffsetImage(new TriangleImage(
        new Posn(-15, 0), new Posn(-15, 15), new Posn(0, 15), 
        "solid", Color.RED), -15, -30, rocket3);

    OverlayImage rocket5 = new OverlayImage(
        new StarImage(50, 7, OutlineMode.SOLID, Color.ORANGE), rocket4);

    IWord activeOut = new ActiveWord("OUT", 1, 700);
    IWord inactiveOut = new InactiveWord("OUT", 1, 700);
    IWord aEmptyOut = new ActiveWord("", 1, 700);
    IWord iaEmptyOut = new InactiveWord("", 1, 0);
    ILoWord mixWordsOut = new ConsLoWord(iaEmptyOut, 
        new ConsLoWord(activeOut, 
            new ConsLoWord(inactiveOut, 
                new ConsLoWord(aEmptyOut, this.empty))));
    ZTypeWorld mixGameEnd = new ZTypeWorld(mixWordsOut, 2);

    return
        //game ends on a mixed world
        t.checkExpect(mixGameEnd.worldEnds(), 
            new WorldEnd(true, mixWordsOut.draw(new WorldScene(400, 600)
                .placeImageXY(rocket4, 200, 520)
                .placeImageXY(gameOver, 200, 300)
                .placeImageXY(rocket5, 200, 520))))
        //game doesn't end on a mixed world
        && t.checkExpect(this.mixWorld.worldEnds(), 
            new WorldEnd(false, this.captainFights.draw(new WorldScene(400, 600)
                .placeImageXY(rocket4, 200, 520))))

        //game ends with words outside
        && t.checkExpect(mixGameEnd.makeAFinalScene(), 
            mixWordsOut.draw(new WorldScene(400, 600)
                .placeImageXY(rocket4, 200, 520)
                .placeImageXY(gameOver, 200, 300)
                .placeImageXY(rocket5, 200, 520)))
        //game ends with words inside
        && t.checkExpect(this.mixWorld.makeAFinalScene(), 
            this.captainFights.draw(new WorldScene(400, 600)
                .placeImageXY(rocket4, 200, 520)
                .placeImageXY(gameOver, 200, 300)
                .placeImageXY(rocket5, 200, 520)))
        ;
  }

  //test for onClickForTesting
  boolean testOnTickForTesting(Tester t) {
    ZTypeWorld mixGameEnd = new ZTypeWorld(this.captainFights, 15);
    return
        true
        //tick satisfies the 15ticks per seconds
        //t.checkExpect(mixGameEnd.onTickForTesting(), mixGameEnd.onTickForTesting())

        //tick does not satisfies the 15 ticks per seconds
        //&& t.checkExpect(this.peaceWorld.onTickForTesting(), this.peaceWorld.onTickForTesting())
        ;
  }


  // Running world program
  boolean testBigBang(Tester t) {
    ZTypeWorld world = new ZTypeWorld(new MtLoWord(), 0);
    int worldWidth = 400;
    int worldHeight = 600;
    double tickRate = 0.1;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }

}