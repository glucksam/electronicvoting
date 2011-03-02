package saddleback_caterpillar.workshop;

public class testerDB {
	private class ballot {
		final String sVote;
		final String sAudit1;
		final String sAudit2;
		final String gInr1;
		final String gInr2;
		final String sig1;
		final String sig2;
		final String sVoteSig;
		final int iID1;
		final int iID2;
		final int iCount1;
		final int iCount2;

		public ballot(String sVote, String sAudit1, String sAudit2,
				String sGInr1, String sGInr2, String sig1, String sig2, String sVoteSig,
				int iID1, int iID2, int iCount1, int iCount2) {
			this.sVote = sVote;
			this.sAudit1 = sAudit1;
			this.sAudit2 = sAudit2;
			this.gInr1 = sGInr1;
			this.gInr2 = sGInr2;
			this.sig1 = sig1;
			this.sig2 = sig2;
			this.sVoteSig = sVoteSig;
			this.iID1 = iID1;
			this.iID2 = iID2;
			this.iCount1 = iCount1;
			this.iCount2 = iCount2;
		}
	}

	private static class ECElGamalParams {
		final static String sCurveName = "p-256";
		final static String sPublicKey = "(b43ac84c7c46cec0fb0d350e6995b1949bad5609da41b78bbd5668ded8fcbdfc, e95cb67f73e64aea3c12fbb6e1b0214942147c0e63e64f4ea2831e3f41183525)";
	}

	private static class ECDSAParams {
		final static String sECDSACurve = "p-192";
		final static String sHash = "NONEwithECDSA";
		final static String sVerificationKey1 = "(3e1b19bc5c76599778c8f40f5709ae2f24e3a6a2ac8276cd, a40da39945f8d22fae52aa57a43862e3f9799be60b2d600d)";
		final static String sVerificationKey2 = "(eaf7f0744df6b42cad38934d4465ace23ef7f567ffb4eb9d, af92301386a2a12caa2db65f35de8ae1d612731e749673a3)";
	}

	private final ballot[] votes;
	private final int numOfCans = 25;

	public testerDB() {
		votes = new ballot[5];
		votes[0] = new ballot(
				"(zhI63hSDhqs9AkZCsdJoe21KvKgT6Jq4x+iBS/K6MjY=,9cJehEjHZLwCd/9fnFF75avgwwxaffCVdpwzr5q2R7I=):(O7c2hfnKPJTybkByk8qAUO9KM+yDWtNTB9HBKYnjGWQ=,FVGuzx0//IsL2bDVXB9HqX4+nxqX37+WlDn5nJEregI=)",
				"ZMUx72/eEz+7ph26sDmsk1Eae9BTpDPt9Yc1x6cFOvQ=",
				"L0KLPZeF/XzKRqed7blURgAkJ37MQGNBZFbNppJbcGo=",
				"(Z16doX7BkA11oiPeOcDBJkaS/D4vRdGWrXKTaLgmP8k=,LLxFYP9TS90KQzegJDYnXXjcjrSxhynvLR1n6Oiyplg=)",
				"(NGu8ed3PRMIb1ZHaecluGkv+XaFMkESgAq4owPUyZfU=,wwNThou2Islc0CIY0aXRAAlQAjrCiZ7nWhngnpvxuDw=)",
				"(AM0MQZ1J5GdJj4FsCbuCn2Lpjslcl96XkA==,AOfW7C1ba1lYTHsoLxpc8/mEcaA7IxSBhw==)",
				"(RFINiWkGLaiE40NFM+Sy7OI9sO1znmq+,AJE1e5JpKRjki+h/RJDy/4Jb3EgpSVr0hw==)",
				"(Hb1d1jD6xDdgfo5Oyh7UhRyHvtbC/eX3,It+Y9kS1BMOqwVso9Ri8F275V3GLfsfO)",
				1111, 2222, 155, 141);

		votes[1] = new ballot(
				"(ZFmpt8TZ9hNOc7zBHs3DF+pmdknCyg1iRMnz2KXvymE=,5/lG8A6GkyDzzyHG25g0ttjIyG5a1EPjNLR2Vzw/k/g=):(TM8UcN4by0HMrukaGMAlnWvYNMrAZKVMAyq2xyB7fDg=,wdJvjsETRlx8jgZzdHuEixZEoMeNhwBrTSLROdt8uT0=)",
				"DvpOWSQx9xXgO6bfkHUYlzURFxYRMAh3ryOC0MujcvU=",
				"YZjGUvNGy5A/ADhcfhvr2ZOkuSLls5z2G6oM0DzK2bo=",
				"(6pemiNsr6NuueDikl8rGBNga82POw+YRMKV4Fe904Yc=,U1w/Zwtt2DQKQIIMhWykGJem9d3huAIdrXOxvB9Zd44=)",
				"(mUV8WCau7mx1DMSmvt+Wne2j9TapYJChht3RtiNT65w=,9RmgC46vlrsl+A4PfEgNUE83rNruBVzprdCxPCgkMUE=)",
				"(AMcdyAJeOolETLs0Dx2UYYy3yzxT4j8xYw==,WG51qVSGkRtIU2D7vhjM1GrO3ZVx38uJ)",
				"(Paef94+ccbCUSb//WDcvMa1tHazJ06JJ,AP0bJeHgxAGHgbzpMJQmUD1hRDhvQsw3KA==)",
				"(eeWQnrfnBEWTv1T4/iGSMqD1xltk6GeF,ALStWWEeM5WMfAp1uDKo2DROeoDKOQIsvg==)",
				1111, 2222, 157, 143);
		votes[2] = new ballot(
				"(m2Qm/17DMmdbPZi7H71DlpR9NPFnhsXFD3bAEUasE5M=,XHESPZqSg3O1JNyrwawdEJy9rpXm9mvwS48uNfSbKkQ=):(IJD5mkeRqavlJ03v6SM7epkT71PHZYA9KJU/QamRaGA=,CIF2Gom1iLpllj2+xIrljQBG0UqFPm80Zafld8V+t1I=)",
				"XD/4EdEzlcrLy3famMqH+SWDxgPUmOmtgTor69gjy4g=",
				"FzhlDaF7AGIyQF+FevDCl36xK/4dd/3+eobj/lVsxlY=",
				"(r8+J+uqng5A9AqC46ZJ4j+zpp9xJV1mFgffvxYcFJdc=,GfHH5uG4vLBdOFNjjRmaPjvI2Fh+I4IdXsuvrmD4G+E=)",
				"(pgysfXfCV0GuVzLcQ3mi4wmQxulG+REpCTpshskhkc8=,oJjkF9X6kmCoOaIkFqxE2KIM8rJT+q0kMhhuPLO0bs4=)",
				"(UfgBnpiEHPNi9m+pWED/zsCz0NQ5bn9H,AOWoF2ng2TTw2t/H7czVXQDWoqrvx1INTw==)",
				"(AJnQQeaYK30VO2w9zzgqsRgCLD6Dv/ROMw==,CR3mIHyroxPGqQgH5Heq61wsyscE6Gox)",
				"(UtJ4iP40d2bZgEN0E9/haqWPyJHxiK/V,ANb2Fo2i24orX3UokQQp/4WC7b9LudDB+w==)",
				1111, 2222, 158, 144);
		votes[3] = new ballot(
				"(j0YmmbVVryPx82L/T/SWxMmeAEYFjiyNjmsijC8RjJQ=,65mIDoSCcNHcyYS1J6CaPvwGIKC621XHyIfZqKwoA4A=):(+4FGAcrWCKh7mMoVWvVWdVya7UA1mMWQQ0q9ZlmqK9k=,Gej6ZYcYUXpEaUsU/cJrn2pq/s1hqvuPSoN/TLTQ8UE=)",
				"Aw9nRI7ECekYvLutErlAnO4OdTfbpI660Q5LDz4G5yQ=",
				"PaMfSpLYUOzr/518MPsF3DBOIt2B6jcjjPfCHubW6r0=",
				"(7oUQomJA3UO3SIJIr+XVQSDUJhPOicuxgn7LAdQh+Fw=,kkzhEyrmCnfWER6FdMLUAJlG/hyxH0Ca+vwBLzRLMYY=)",
				"(XRp5o35K5LU8FaBqpYLK6aBOjnTec7TvW4+v4T4Iquo=,6G6tsEZ6+GwpCXrCZab1Pv74ATtaFhkbaeEU4avnhGI=)",
				"(fOKli8xhIG3ZVNPU1m0Qx+9oGIWfK2NC,AM/K0NSfYUyGjYLcPpmIlkxrj+/Qi9v7Ug==)",
				"(ZDlIZmAS+Hk2A5HIela2oLIWttAhVv4c,a88/MuZ9Ptca1VhmD0N/2dEYyn17Mt8q)",
				"(AOhTzOwHB2wULsjSqKUykCW5nO0oHyiYdQ==,Q+w90HeUG68bOP4GjDSUzNvwP4pM4Its)",
				1111, 2222, 159, 145);
		votes[4] = new ballot(
				"(hgFYk9v192wdah1qkCIA6KwmmlMnsMASCAFL14iKIrg=,jAwd+/4nth7wkoBQUAuT2xbf9LSU1FF0MT/L1sE7OZs=):(7QTT5KNsNc7dXeJ4pFzYnso8X99/q3dAzzH0pIiwnfg=,HrfJm/W/TcV3p7Z7qEaKJzUuxHRmQqqMyH7fLR9U9iQ=)",
				"Vy/c2VFsBq5ZfOGKWkL+AJDXpfNig0VaCiZtqrNxKhw=",
				"JiQR3fmWPTkwReAHTAoRDDY7C0wTTcl15XHTpK8msoU=",
				"(CWpplNQDw2FQ8c/qZDrHnrkdzRB+qo2dMdSYLx6kchs=,eRViFxjjOKp6UGWQWby69r+bx62I8jHxVnx5ermKuIo=)",
				"(wqyzZwul53JkCsn57N5imLhcRSieXh8fAioNu/brqO0=,Dg9TxjF2JXEB9Jt0dlzgtrTz9PbwqM9BxhNkqMf9rY8=)",
				"(AJmYn0p64t36qmLCSekBZqApV9cqVp+FxA==,YgbhMP5DbyCbgc9/ttZQkpf/L+MIY/wr)",
				"(DcQU9YE/bU7jbF8fidV47tSzeTFm6TSo,CFAIzl1Bcna4rWyjGKtxZt2hT6gZ8qgX)",
				"(AMxrkqu8pGWY2tOeH6nG7NJd2YzppHK0tA==,APspsWJk1PkPlSwhEX+NLZ1/bb/J4ge2tg==)",
				1111, 2222, 336, 301);
	}

	public String getVote(int index) {
		return this.votes[index - 1].sVote;

	}

	public String getAudit1(int index) {
		return this.votes[index - 1].sAudit1;
	}

	public String getAudit2(int index) {
		return this.votes[index - 1].sAudit2;
	}

	public String getGInr1(int index) {
		return this.votes[index - 1].gInr1;
	}

	public String getGInr2(int index) {
		return this.votes[index - 1].gInr2;
	}

	public String getSig1(int index) {
		return this.votes[index - 1].sig1;
	}

	public String getSig2(int index) {
		return this.votes[index - 1].sig2;
	}
	
	public String getVoteSig(int index){
		return this.votes[index - 1].sVoteSig;
	}

	public int getID1(int index) {
		return this.votes[index - 1].iID1;
	}

	public int getID2(int index) {
		return this.votes[index - 1].iID2;
	}

	public int getCount1(int index) {
		return this.votes[index - 1].iCount1;
	}

	public int getCount2(int index) {
		return this.votes[index - 1].iCount2;
	}

	public int getCandidateNum() {
		return this.numOfCans;
	}

	public String getEGPublicKey() {
		return ECElGamalParams.sPublicKey;
	}

	public String getEGCurve() {
		return ECElGamalParams.sCurveName;
	}

	public String getECDSACurve() {
		return ECDSAParams.sECDSACurve;
	}

	public String getECDSAHash() {
		return ECDSAParams.sHash;
	}

	public String getVerificayionKey1() {
		return ECDSAParams.sVerificationKey1;
	}

	public String getVerificayionKey2() {
		return ECDSAParams.sVerificationKey2;
	}
}
