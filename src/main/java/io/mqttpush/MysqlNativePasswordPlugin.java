package io.mqttpush;

/*
Copyright (c) 2012, 2015, Oracle and/or its affiliates. All rights reserved.

The MySQL Connector/J is licensed under the terms of the GPLv2
<http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>, like most MySQL Connectors.
There are special exceptions to the terms and conditions of the GPLv2 as it is applied to
this software, see the FOSS License Exception
<http://www.mysql.com/about/legal/licensing/foss-exception.html>.

This program is free software; you can redistribute it and/or modify it under the terms
of the GNU General Public License as published by the Free Software Foundation; version 2
of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this
program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
Floor, Boston, MA 02110-1301  USA

*/

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * MySQL Native Password Authentication Plugin
 */
public class MysqlNativePasswordPlugin {

	private String password = null;

	private String passwordEncoding;

	public void destroy() {
		this.password = null;
	}

	public String getProtocolPluginName() {
		return "mysql_native_password";
	}

	public boolean requiresConfidentiality() {
		return false;
	}

	public boolean isReusable() {
		return true;
	}

	public void setAuthenticationParameters(String user, String password) {
		this.password = password;
	}

	public void setPasswordEncoding(String passwordEncoding) {
		this.passwordEncoding = passwordEncoding;
	}

	public byte[] nextAuthenticationStep(String seed){

		try {
			
			String pwd = this.password;

			if (seed != null &&pwd != null &&pwd.length() > 0) {
				return Security.scramble411(pwd, seed, passwordEncoding);
			} 

		} catch (NoSuchAlgorithmException nse) {
			throw new RuntimeException(nse);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return null;
	}

}
