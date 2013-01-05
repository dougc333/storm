package test4.demo.bolts;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.mortbay.jetty.handler.AbstractHandler;

public class ServerBolt extends AbstractHandler {

	public static void main(String[] args) {

	}

	@Override
	public void handle(String arg0, HttpServletRequest request,
			HttpServletResponse response, int arg3) {
		// TODO Auto-generated method stub
		try {
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			// request.setHandled(true);
			String reqParam = request.getParameter("first");

			Enumeration<String> e = request.getParameterNames();
			String[] values = null;
			while (e.hasMoreElements()) {
				String pName = e.nextElement();
				values = request.getParameterValues(pName);
				System.out
						.println("paramName:" + pName + " value:" + values[0]);

			}
			if (values != null && values[0] != null) {
				response.getWriter().write("asdfasdf" + values[0]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
