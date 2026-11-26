import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a bear.
 * Bears age, move, eat foxes, and die.
 * 
 * Bears are assumed to hang out closer to where foxes exist, and the relatively larger litter
 * size, higher food value from foxes, and longer maximum life span will let the bear 
 * population stay afloat as the fox populations die and grow over time.
 * 
 * We are assuming that rabbits are too fast for a bear to catch, hence foxes are better for
 * bears to hunt both in the lower speed of foxes and having more meat on them (food value).
 * 
 * @author Kevin Cox-Torrelio
 * @version 11-25-2026
 */
public class Bear extends Animal
{
    

    private static final int BREEDING_AGE = 20;

    private static final int MAX_AGE = 250;

    private static final double BREEDING_PROBABILITY = 0.08;

    private static final int MAX_LITTER_SIZE = 4;
    
    private static final int FOX_FOOD_VALUE = 22;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    private int foodLevel;

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            foodLevel = rand.nextInt(FOX_FOOD_VALUE);
        }
        else {
            setAge(0);
            foodLevel = FOX_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the bear does most of the time: it hunts for
     * foxes. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newBears A list to return newly born foxes.
     */
    public void act(List<Animal> newBears)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newBears);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    
    /**
     * Make this bear more hungry. This could result in the bear's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for foxes adjacent to the current location.
     * Only the first live fox is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Fox) {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) { 
                    fox.setDead();
                    foodLevel = FOX_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this bear is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBears A list to return newly born bears.
     */
    private void giveBirth(List<Animal> newBears)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Bear young = new Bear(false, field, loc);
            newBears.add(young);
        }
    }
        
    

    /**
     * Getter method to get the breeding age
     * 
     * @return The age at which a fox starts to breed.
     */
    public int getBreedingAge(){
        return BREEDING_AGE;
    }
    
    /**
     * Getter method to get the max age
     * 
     * @return the max age of the fox
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
    
    /**
     * Gets the breeding probability of the fox
     * 
     * @return the breeding probability of the fox
     */
    public double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Gets the maximum litter size for a fox
     * 
     * @return the maximum litter size for a fox
     */
    public int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }
    
    
}
