package android.server;

public class DBHelper {
	
	private String uname;
	private String pwd;
	private String mobile;
	private String work;
	private String home;
	private String mail;
	private String im;
	private String geo;
	
	//Questa Stringa può assumere i valori HOME WORK MAIL IM o MOBILE, di default per ogni utente è settata ad MOBILE
	private String preferredMode;

	public DBHelper(String uname, String pwd, String mobile, String work,
			String home, String mail, String im, String geo,
			String preferredMode) {
		super();
		this.uname = uname;
		this.pwd = pwd;
		this.mobile = mobile;
		this.work = work;
		this.home = home;
		this.mail = mail;
		this.im = im;
		this.geo = geo;
		this.preferredMode = preferredMode;
	}

	public String getUname() {
		return uname;
	}

	public String getPwd() {
		return pwd;
	}

	public String getMobile() {
		return mobile;
	}

	public String getWork() {
		return work;
	}

	public String getHome() {
		return home;
	}

	public String getMail() {
		return mail;
	}

	public String getIm() {
		return im;
	}

	public String getGeo() {
		return geo;
	}

	public String getPreferredMode() {
		return preferredMode;
	}

	
}
