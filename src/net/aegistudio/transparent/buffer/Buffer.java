package net.aegistudio.transparent.buffer;

import net.aegistudio.transparent.EnumDataType;

/**
 * Data class which could do something related to buffer.
 * @author aegistudio
 */

public interface Buffer {
	public void bufferData(int target, int usage);
	public int getElements();
	public EnumDataType getType();
}
