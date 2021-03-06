/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * If-else statement. Node type is {@link Token#IF}.
 * <p>
 *
 * <pre>
 * <i>IfStatement</i> :
 *       <b>if</b> ( Expression ) Statement <b>else</b> Statement
 *       <b>if</b> ( Expression ) Statement
 * </pre>
 */
public class IfStatement extends AstNode {

    private AstNode condition;
    private AstNode thenPart;
    private int elsePosition = -1;
    private AstNode elsePart;
    private int lp = -1;
    private int rp = -1;

    {
	type = Token.IF;
    }

    public IfStatement() {
    }

    public IfStatement(int pos) {
	super(pos);
    }

    public IfStatement(int pos, int len) {
	super(pos, len);
    }

    /**
     * @return This node as a JSON object in Esprima format.
     * @author qhanam
     */
    @Override
    public JsonObject getJsonObject() {
	JsonObject object = new JsonObject();
	object.addProperty("type", "IfStatement");
	object.add("test", this.getCondition().getJsonObject());
	object.add("consequent", this.getThenPart().getJsonObject());
	if (this.getElsePart() != null)
	    object.add("alternate", this.getElsePart().getJsonObject());
	else
	    object.add("alternate", JsonNull.INSTANCE);
	object.add("criteria", getCriteriaAsJson());
	object.add("dependencies", getDependenciesAsJson());
	object.addProperty("change", changeType.toString());
	object.addProperty("change-noprop", changeTypeNoProp.toString());
	return object;
    }

    /**
     * Clones the AstNode.
     * 
     * @return The clone of the AstNode.
     * @throws CloneNotSupportedException
     */
    @Override
    public AstNode clone(AstNode parent) {

	/* Get the shallow clone. */
	IfStatement clone = (IfStatement) super.clone();
	clone.setParent(parent);
	clone.moved = this.moved;
	clone.changeType = this.changeType;
	clone.changeTypeNoProp = this.changeTypeNoProp;
	clone.fixedPosition = this.fixedPosition;
	clone.ID = this.ID;

	/* Clone the children. */
	AstNode condition = null;
	AstNode then = null;
	AstNode els = null;

	if (this.getCondition() != null)
	    condition = this.getCondition().clone(clone);
	if (this.getThenPart() != null)
	    then = this.getThenPart().clone(clone);
	if (this.getElsePart() != null)
	    els = this.getElsePart().clone(clone);

	clone.setCondition(condition);
	clone.setThenPart(then);
	clone.setElsePart(els);

	return clone;

    }

    /**
     * Returns if condition
     */
    public AstNode getCondition() {
	return condition;
    }

    /**
     * Sets if condition.
     * 
     * @throws IllegalArgumentException
     *             if {@code condition} is {@code null}.
     */
    public void setCondition(AstNode condition) {
	assertNotNull(condition);
	this.condition = condition;
	condition.setParent(this);
    }

    /**
     * Returns statement to execute if condition is true
     */
    public AstNode getThenPart() {
	return thenPart;
    }

    /**
     * Sets statement to execute if condition is true
     * 
     * @throws IllegalArgumentException
     *             if thenPart is {@code null}
     */
    public void setThenPart(AstNode thenPart) {
	assertNotNull(thenPart);
	this.thenPart = thenPart;
	thenPart.setParent(this);
    }

    /**
     * Returns statement to execute if condition is false
     */
    public AstNode getElsePart() {
	return elsePart;
    }

    /**
     * Sets statement to execute if condition is false
     * 
     * @param elsePart
     *            statement to execute if condition is false. Can be {@code null}.
     */
    public void setElsePart(AstNode elsePart) {
	this.elsePart = elsePart;
	if (elsePart != null)
	    elsePart.setParent(this);
    }

    /**
     * Returns position of "else" keyword, or -1
     */
    public int getElsePosition() {
	return elsePosition;
    }

    /**
     * Sets position of "else" keyword, -1 if not present
     */
    public void setElsePosition(int elsePosition) {
	this.elsePosition = elsePosition;
    }

    /**
     * Returns left paren offset
     */
    public int getLp() {
	return lp;
    }

    /**
     * Sets left paren offset
     */
    public void setLp(int lp) {
	this.lp = lp;
    }

    /**
     * Returns right paren position, -1 if missing
     */
    public int getRp() {
	return rp;
    }

    /**
     * Sets right paren position, -1 if missing
     */
    public void setRp(int rp) {
	this.rp = rp;
    }

    /**
     * Sets both paren positions
     */
    public void setParens(int lp, int rp) {
	this.lp = lp;
	this.rp = rp;
    }

    @Override
    public String toSource(int depth) {
	String pad = makeIndent(depth);
	StringBuilder sb = new StringBuilder(32);
	sb.append(pad);
	sb.append("if (");
	sb.append(condition.toSource(0));
	sb.append(") ");
	if (thenPart.getType() != Token.BLOCK) {
	    sb.append("\n").append(makeIndent(depth + 1));
	}
	sb.append(thenPart.toSource(depth).trim());
	if (elsePart != null) {
	    if (thenPart.getType() != Token.BLOCK) {
		sb.append("\n").append(pad).append("else ");
	    } else {
		sb.append(" else ");
	    }
	    if (elsePart.getType() != Token.BLOCK && elsePart.getType() != Token.IF) {
		sb.append("\n").append(makeIndent(depth + 1));
	    }
	    sb.append(elsePart.toSource(depth).trim());
	}
	sb.append("\n");
	return sb.toString();
    }

    /**
     * Visits this node, the condition, the then-part, and if supplied, the
     * else-part.
     */
    @Override
    public void visit(NodeVisitor v) {
	if (v.visit(this)) {
	    condition.visit(v);
	    thenPart.visit(v);
	    if (elsePart != null) {
		elsePart.visit(v);
	    }
	}
    }

    @Override
    public boolean isStatement() {
	return true;
    }
}
