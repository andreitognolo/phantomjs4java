package phantomjs4java;

import java.util.Arrays;

public class PhantomJSExec {

	private static final Long TIMEOUT = 5000l;

	private String source;
	private String eval;
	private String[] args = new String[0];
	private Long timeout = TIMEOUT;

	public static PhantomJSExec create() {
		return new PhantomJSExec();
	}

	public PhantomJSExec eval(String script) {
		this.eval = script;
		return this;
	}

	public PhantomJSExec args(String... args) {
		this.args = args;
		if (this.args == null) {
			this.args = new String[0];
		}
		return this;
	}

	public PhantomJSExec timeout(Long timeout) {
		this.timeout = timeout;
		if (this.timeout == null) {
			this.timeout = TIMEOUT;
		}
		return this;
	}

	public PhantomJSExec source(String source) {
		this.source = source;
		return this;
	}

	public String getSource() {
		return source;
	}

	public String getEval() {
		return eval;
	}

	public String[] getArgs() {
		return args;
	}

	public Long getTimeout() {
		return timeout;
	}

	@Override
	public String toString() {
		return "[PhantomJSExec source=" + source + ", eval=" + eval + ", args=" + Arrays.toString(args) + ", timeout=" + timeout + "]";
	}

}
