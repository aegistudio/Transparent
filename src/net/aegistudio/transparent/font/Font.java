package net.aegistudio.transparent.font;

public interface Font {
	public CharacterMap getCharacterMap();
	
	public void install(String parent);
	
	public void uninstall(String parent);
}
