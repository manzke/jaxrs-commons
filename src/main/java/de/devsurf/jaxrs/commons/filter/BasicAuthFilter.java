/**
 * Copyright (C) 2010 Daniel Manzke <daniel.manzke@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.devsurf.jaxrs.commons.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sun.misc.BASE64Decoder;

/**
 * Windows Vista: BasicAuth is disabled
 * 
	Windows Vista will fail to connect to server using insecure Basic authentication. 
	It will not even display any login dialog. Vista requires SSL / HTTPS connection to be used with Basic. 
	However you still can connect using Vista if you set the following registry key on a client machine: 
	HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\WebClient\Parameters\BasicAuthLevel to 2. 
	The BasicAuthLevel can be set to the following values: 
		0 - Basic authentication disabled 
		1 - Basic authentication enabled for SSL shares only 
		2 or greater - Basic authentication enabled for SSL shares and for non-SSL shares 
 * 
 * @author Daniel Manzke
 *
 */
public class BasicAuthFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = ((HttpServletRequest)req);
		HttpServletResponse response = ((HttpServletResponse)res);
		String auth = request.getHeader("Authorization");
		
		if(auth == null){
			response.setHeader("WWW-Authenticate", "BASIC realm=\"SAPERION\"");
			response.setStatus(401);

			return;
		}else{
			try{
				BASE64Decoder decoder = new BASE64Decoder();
				String sNameAndPw = new String(decoder.decodeBuffer(auth.substring(6).trim()));
				String username = sNameAndPw.substring(0, sNameAndPw.indexOf(":"));
				String password = sNameAndPw.substring(sNameAndPw.indexOf(":")+1);
				
				HttpSession session = request.getSession(true);
				session.setAttribute("BasicAuthUser", username);
				session.setAttribute("BasicAuthPassword", password);
				//authenticate -> if wrong send 401
			}catch (java.io.IOException ioe){
			}
		}
		
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
