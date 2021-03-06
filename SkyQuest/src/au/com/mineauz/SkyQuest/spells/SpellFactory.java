package au.com.mineauz.SkyQuest.spells;

import java.util.HashMap;

/**
 * A factory for creating spell instances by type name.
 * Spells should be statically registered with this factory using registerSpellName()
 * @author Schmoller
  */
public class SpellFactory 
{
	/**
	 * Gets an instance of a registered spell by name
	 * @param name The case insensitive name of the spell
	 * @return An instance of the spell if there was one with that name, null otherwise
	 */
	public static SpellBase getSpell(String name)
	{
		if(!sRegisteredSpells.containsKey(name.toLowerCase()))
		{
			throw new IllegalArgumentException(name + " has not been registered with SpellFactory");
		}
			
			
		
		try 
		{
			return sRegisteredSpells.get(name.toLowerCase()).newInstance();
		} 
		catch (InstantiationException | IllegalAccessException e) 
		{
			throw new IllegalArgumentException(sRegisteredSpells.get(name.toLowerCase()) + " has not been setup correctly. It needs to have a default contructor.");
		}
	}
	
	/**
	 * Gets the type name of a registered spell
	 */
	public static String getSpellType(Class<? extends SpellBase> classType)
	{
		if(!sRegisteredSpellsReverse.containsKey(classType))
			throw new IllegalArgumentException(classType.getName() + " has not been registered with SpellFactory");
		return sRegisteredSpellsReverse.get(classType);
	}
	
	/**
	 * Registers a spell type to the name so it can be used with getSpell()
	 * @param name The name to give the spell
	 * @param classType The class of the spell
	 * @return True if the registration was successful
	 */
	public static boolean registerSpellName(String name, Class<? extends SpellBase> classType)
	{
		if(sRegisteredSpells.containsKey(name.toLowerCase()))
			return false;
		
		sRegisteredSpells.put(name.toLowerCase(), classType);
		sRegisteredSpellsReverse.put(classType, name.toLowerCase());
		
		return true;
	}
	
	private static HashMap<String, Class<? extends SpellBase>> sRegisteredSpells = new HashMap<>();
	private static HashMap<Class<? extends SpellBase>, String> sRegisteredSpellsReverse = new HashMap<>();
	
	static
	{
		registerSpellName("save", SavePointSpell.class);
		registerSpellName("warp", WarpSpell.class);
	}
}
