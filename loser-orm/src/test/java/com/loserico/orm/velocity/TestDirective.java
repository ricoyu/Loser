package com.loserico.orm.velocity;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class TestDirective extends Directive {

	private static final VelocityEngine velocityEngine = new VelocityEngine();

	@Override
	public String getName() {
		return "test";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer,
			Node node) throws IOException, ResourceNotFoundException,
			ParseErrorException, MethodInvocationException {
		SimpleNode sn = (SimpleNode) node.jjtGetChild(0);
		String value = (String) sn.value(context);
		sn = (SimpleNode) node.jjtGetChild(1);
		Serializable s = (Serializable) sn.value(context);

		sn = (SimpleNode) node.jjtGetChild(2);
		Object data = sn.value(context);
		Map map = new HashMap();
		List<String> strings = new ArrayList<String>();
		strings.add("MSN");
		strings.add("QQ");
		strings.add("Gtalk");
		map.put("data", strings);
		String vel = "#foreach($element in $data) \n<li>$element</li>\n  #end";
		writer.write(renderTemplate(map, vel));
		return true;
	}

	public static String renderTemplate(Map params, String vimStr) {
		VelocityContext context = new VelocityContext(params);
		StringWriter writer = new StringWriter();
		velocityEngine.evaluate(context, writer, "", vimStr);
		return writer.toString();
	}
}