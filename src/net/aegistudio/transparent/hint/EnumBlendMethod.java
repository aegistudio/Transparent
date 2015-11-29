package net.aegistudio.transparent.hint;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public enum EnumBlendMethod {
	REPLACE(GL11.GL_ONE, GL11.GL_ZERO, GL14.GL_FUNC_ADD, "%result = %source"),
	ACCUMULATE(GL11.GL_ONE, GL11.GL_ONE, GL14.GL_FUNC_ADD, "%result = %source + %destination"),
	MODULATE(GL11.GL_SRC_COLOR, GL11.GL_ZERO, GL14.GL_FUNC_ADD, "%result = %source * %destination"),
	DECALATE(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL14.GL_FUNC_ADD, 
			"%result = %source * %source.a + %destination * (1.0 - %source.a)"),
	REVERSE_DECALATE(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA, GL14.GL_FUNC_ADD,
			"%result = %source * %destination.a * (1.0 - %destination.a)");
	
	private final int sourceFactor;
	private final int destinationFactor;
	private final int equation;
	private final String formula;
	
	private EnumBlendMethod(int sourceFactor, int destinationFactor, int equation, String formula) {
		this.sourceFactor = sourceFactor;
		this.destinationFactor = destinationFactor;
		this.equation = equation;
		this.formula = formula;
	}
	
	public String getFormula() {
		return this.formula;
	}
	
	public int getEquation() {
		return this.equation;
	}
	
	public void set() {
		GL11.glBlendFunc(sourceFactor, destinationFactor);
		GL14.glBlendEquation(equation);
	}
}
