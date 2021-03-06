/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

import com.google.gson.JsonObject;

/**
 * Do statement. Node type is {@link Token#DO}.
 * <p>
 *
 * <pre>
 * <i>DoLoop</i>:
 * <b>do</b> Statement <b>while</b> <b>(</b> Expression <b>)</b> <b>;</b>
 * </pre>
 */
public class DoLoop extends Loop {

    private AstNode condition;
    private int whilePosition = -1;

    {
	type = Token.DO;
    }

    public DoLoop() {
    }

    public DoLoop(int pos) {
	super(pos);
    }

    public DoLoop(int pos, int len) {
	super(pos, len);
    }

    /**
     * @return This node as a JSON object in Esprima format.
     * @author qhanam
     */
    @Override
    public JsonObject getJsonObject() {
	JsonObject object = new JsonObject();
	object.addProperty("type", "DoWhileStatement");
	object.add("test", this.getCondition().getJsonObject());
	object.add("body", this.getBody().getJsonObject());
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
	DoLoop clone = (DoLoop) super.clone();
	clone.setParent(parent);
	clone.moved = this.moved;
	clone.changeType = this.changeType;
	clone.changeTypeNoProp = this.changeTypeNoProp;
	clone.fixedPosition = this.fixedPosition;
	clone.ID = this.ID;

	/* Clone the children. */
	AstNode condition = null;
	AstNode body = null;

	if (this.getCondition() != null)
	    condition = this.getCondition().clone(clone);
	if (this.getBody() != null)
	    body = this.getBody().clone(clone);

	clone.setCondition(condition);
	clone.setBody(body);

	return clone;

    }

    /**
     * Returns loop condition
     */
    public AstNode getCondition() {
	return condition;
    }

    /**
     * Sets loop condition, and sets its parent to this node.
     * 
     * @throws IllegalArgumentException
     *             if condition is null
     */
    public void setCondition(AstNode condition) {
	assertNotNull(condition);
	this.condition = condition;
	condition.setParent(this);
    }

    /**
     * Returns source position of "while" keyword
     */
    public int getWhilePosition() {
	return whilePosition;
    }

    /**
     * Sets source position of "while" keyword
     */
    public void setWhilePosition(int whilePosition) {
	this.whilePosition = whilePosition;
    }

    @Override
    public String toSource(int depth) {
	StringBuilder sb = new StringBuilder();
	sb.append(makeIndent(depth));
	sb.append("do ");
	sb.append(body.toSource(depth).trim());
	sb.append(" while (");
	sb.append(condition.toSource(0));
	sb.append(");\n");
	return sb.toString();
    }

    /**
     * Visits this node, the body, and then the while-expression.
     */
    @Override
    public void visit(NodeVisitor v) {
	if (v.visit(this)) {
	    body.visit(v);
	    condition.visit(v);
	}
    }
}
