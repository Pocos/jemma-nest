package it.telecomitalia.ah.nest;

import org.energy_home.jemma.ah.hac.IHacDevice;

public interface NestHacDevice extends IHacDevice{
	
	public boolean setListener(NestDeviceListener nestServiceCluster);
	
	public boolean removeListener(NestDeviceListener nestServiceCluster);

}
