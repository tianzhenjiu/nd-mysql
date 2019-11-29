package io.mqttpush.mysql.common;

public class Capbilities {
	
	
	
	public static final int CLIENT_LONG_PASSWORD = 0x00000001; /* new more secure passwords */
	public static final int CLIENT_FOUND_ROWS = 0x00000002;
	public static final int CLIENT_LONG_FLAG = 0x00000004; /* Get all column flags */
	public static final int CLIENT_CONNECT_WITH_DB = 0x00000008;
	public static final int CLIENT_COMPRESS = 0x00000020; /* Can use compression protcol */
	public static final int CLIENT_LOCAL_FILES = 0x00000080; /* Can use LOAD DATA LOCAL */
	public static final int CLIENT_PROTOCOL_41 = 0x00000200; // for > 4.1.1
	public static final int CLIENT_INTERACTIVE = 0x00000400;
	public static final int CLIENT_SSL = 0x00000800;
	public static final int CLIENT_TRANSACTIONS = 0x00002000; // Client knows about transactions
	public static final int CLIENT_RESERVED = 0x00004000; // for 4.1.0 only
	public static final int CLIENT_SECURE_CONNECTION = 0x00008000;
	public static final int CLIENT_MULTI_STATEMENTS = 0x00010000; // Enable/disable multiquery support
	public static final int CLIENT_MULTI_RESULTS = 0x00020000; // Enable/disable multi-results
	public static final int CLIENT_PLUGIN_AUTH = 0x00080000;

	public static final int CLIENT_CONNECT_ATTRS = 0x00100000;
	public static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 0x00200000;
	public static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 0x00400000;
	public static final int CLIENT_SESSION_TRACK = 0x00800000;
	public static final int CLIENT_DEPRECATE_EOF = 0x01000000;
	
	
	public static final int SERVER_SESSION_STATE_CHANGED=0x4000;
	
	
	/**
	 * 
	 * 
	 * SERVER_STATUS_IN_TRANS	0x0001	a transaction is active
SERVER_STATUS_AUTOCOMMIT	0x0002	auto-commit is enabled
SERVER_MORE_RESULTS_EXISTS	0x0008	 
SERVER_STATUS_NO_GOOD_INDEX_USED	0x0010	 
SERVER_STATUS_NO_INDEX_USED	0x0020	 
SERVER_STATUS_CURSOR_EXISTS	0x0040	Used by Binary Protocol Resultset to signal that COM_STMT_FETCH must be used to fetch the row-data.
SERVER_STATUS_LAST_ROW_SENT	0x0080	 
SERVER_STATUS_DB_DROPPED	0x0100	 
SERVER_STATUS_NO_BACKSLASH_ESCAPES	0x0200	 
SERVER_STATUS_METADATA_CHANGED	0x0400	 
SERVER_QUERY_WAS_SLOW	0x0800	 
SERVER_PS_OUT_PARAMS	0x1000	 
SERVER_STATUS_IN_TRANS_READONLY	0x2000	in a read-only transaction
SERVER_SESSION_STATE_CHANGED	0x4000
	 */

}
