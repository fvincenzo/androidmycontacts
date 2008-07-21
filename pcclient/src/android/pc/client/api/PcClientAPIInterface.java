package android.pc.client.api;

public interface PcClientAPIInterface {

	/**
	 * E' solo un'api parziale utile a dimostrare le potenzialità del progetto 
	 */
	
	public abstract void connect();

	public abstract boolean mail();

	public abstract boolean im();

	public abstract void disconnect();

}