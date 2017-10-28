package com.seezoon.eagle.httpclient;

/**
 * 
 * @author hdf 2017年10月21日
 */
public class HttpClientConfig {
	// 连接超时 ms
	private int connectTimeout;
	// 获取数据超时 ms
	private int socketTimeout;
	// 获取连接超时ms
	private int connectionRequestTimeout;
	// 最大线程数
	private int maxTotal;
	// 站点最大连接数
	private int maxPerRoute;
	// 不活跃多久检查ms
	private int validateAfterInactivity;
	// 重试次数 0 不重试
	private int retyTimes;
	// 空闲时间多久销毁
	private int idleTimeToDead;
	// 连接最多存活多久
	private int connTimeToLive;

	public HttpClientConfig(int connectTimeout, int socketTimeout, int connectionRequestTimeout, int maxTotal,
			int maxPerRoute, int validateAfterInactivity, int retyTimes, int idleTimeToDead, int connTimeToLive) {
		super();
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
		this.connectionRequestTimeout = connectionRequestTimeout;
		this.maxTotal = maxTotal;
		this.maxPerRoute = maxPerRoute;
		this.validateAfterInactivity = validateAfterInactivity;
		this.retyTimes = retyTimes;
		this.idleTimeToDead = idleTimeToDead;
		this.connTimeToLive = connTimeToLive;
	}

	public static Builder custom() {
		return new Builder();
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}

	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxPerRoute() {
		return maxPerRoute;
	}

	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}

	public int getValidateAfterInactivity() {
		return validateAfterInactivity;
	}

	public void setValidateAfterInactivity(int validateAfterInactivity) {
		this.validateAfterInactivity = validateAfterInactivity;
	}

	public int getRetyTimes() {
		return retyTimes;
	}

	public void setRetyTimes(int retyTimes) {
		this.retyTimes = retyTimes;
	}

	public int getIdleTimeToDead() {
		return idleTimeToDead;
	}

	public void setIdleTimeToDead(int idleTimeToDead) {
		this.idleTimeToDead = idleTimeToDead;
	}

	public int getConnTimeToLive() {
		return connTimeToLive;
	}

	public void setConnTimeToLive(int connTimeToLive) {
		this.connTimeToLive = connTimeToLive;
	}

	public static class Builder {
		public HttpClientConfig build(){
			return new HttpClientConfig(connectTimeout, socketTimeout, connectionRequestTimeout, 
					maxTotal, maxPerRoute, validateAfterInactivity, retyTimes, idleTimeToDead, connTimeToLive);
		}
		// 连接超时 ms
		private int connectTimeout = 6 * 1000;
		// 获取数据超时 ms
		private int socketTimeout = 6 * 1000;
		// 获取连接超时ms
		private int connectionRequestTimeout = 6 * 1000;
		// 最大线程数
		private int maxTotal = 10;
		// 站点最大连接数
		private int maxPerRoute = 10;
		// 不活跃多久检查ms
		private int validateAfterInactivity = 60 * 1000;
		// 重试次数 0 不重试
		private int retyTimes = 3;
		// 空闲时间多久销毁
		private int idleTimeToDead = 120 * 1000;
		// 连接最多存活多久
		private int connTimeToLive = 300 * 1000;

		public int getConnectTimeout() {
			return connectTimeout;
		}

		public void setConnectTimeout(int connectTimeout) {
			this.connectTimeout = connectTimeout;
		}

		public int getSocketTimeout() {
			return socketTimeout;
		}

		public void setSocketTimeout(int socketTimeout) {
			this.socketTimeout = socketTimeout;
		}

		public int getConnectionRequestTimeout() {
			return connectionRequestTimeout;
		}

		public void setConnectionRequestTimeout(int connectionRequestTimeout) {
			this.connectionRequestTimeout = connectionRequestTimeout;
		}

		public int getMaxTotal() {
			return maxTotal;
		}

		public void setMaxTotal(int maxTotal) {
			this.maxTotal = maxTotal;
		}

		public int getMaxPerRoute() {
			return maxPerRoute;
		}

		public void setMaxPerRoute(int maxPerRoute) {
			this.maxPerRoute = maxPerRoute;
		}

		public int getValidateAfterInactivity() {
			return validateAfterInactivity;
		}

		public void setValidateAfterInactivity(int validateAfterInactivity) {
			this.validateAfterInactivity = validateAfterInactivity;
		}

		public int getRetyTimes() {
			return retyTimes;
		}

		public void setRetyTimes(int retyTimes) {
			this.retyTimes = retyTimes;
		}

		public int getIdleTimeToDead() {
			return idleTimeToDead;
		}

		public void setIdleTimeToDead(int idleTimeToDead) {
			this.idleTimeToDead = idleTimeToDead;
		}

		public int getConnTimeToLive() {
			return connTimeToLive;
		}

		public void setConnTimeToLive(int connTimeToLive) {
			this.connTimeToLive = connTimeToLive;
		}

	}
}
