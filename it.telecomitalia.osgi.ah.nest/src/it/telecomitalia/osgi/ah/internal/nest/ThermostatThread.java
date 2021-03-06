package it.telecomitalia.osgi.ah.internal.nest;

import it.telecomitalia.ah.nest.NestDevice;
import it.telecomitalia.osgi.ah.internal.nest.lib.FieldContainer;
import it.telecomitalia.osgi.ah.internal.nest.lib.FieldValues;
import it.telecomitalia.osgi.ah.internal.nest.lib.JNest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ThermostatThread implements Runnable {
	private Map<String, FieldValues> field_list = new HashMap<String, FieldValues>();
	private JNest jn;
	private NestDevice dev;
	private String deviceId;
	private String structureId;

	// private static Logger LOG =
	// LoggerFactory.getLogger(DiscoveryThread.class);

	public ThermostatThread(NestDevice dev, JNest jn, String deviceId, String structureId) {
		this.jn = jn;
		// The child thread will dispatch his own callback messages
		this.dev = dev;
		this.deviceId = deviceId;
		this.structureId = structureId;
		// Update field hashmap. From this I build the json for the poll
		// request.
		// I will be notified only on the following fields. If you wish to add
		// another field simply add a new FieldValues element,
		// if you wish to add another value for the field simply add it on the
		// FieldElement class.
		FieldValues fv = new FieldValues();
		fv.object_key = "shared." + deviceId;
		fv.object_revision = 0; // this zero-values will be update with the
								// correct values in the Rest poll-response
		fv.object_timestamp = 0;
		field_list.put(fv.object_key, fv);
		fv = new FieldValues();
		fv.object_key = "structure." + structureId;
		fv.object_revision = 0;
		fv.object_timestamp = 0;
		field_list.put(fv.object_key, fv);
		fv = new FieldValues();
		fv.object_key = "device." + deviceId;
		fv.object_revision = 0;
		fv.object_timestamp = 0;
		field_list.put(fv.object_key, fv);
	}

	public void run() {
		while (!Thread.interrupted()) {
			// Build the Json for the long poll request
			FieldContainer fc = new FieldContainer();
			fc.objects = new ArrayList<FieldValues>(field_list.values());
			fc.timeout = "1017";
			fc.session = "2163221.85759.1445870206001";
			Gson gson = new Gson();
			String poll_json = gson.toJson(fc);
			String result = jn.longPollingNest(poll_json);

			// Parse the result and update object_timestamp and
			// object_revision
			// for next poll request

			JsonElement jelement = new JsonParser().parse(result);
			JsonObject jobject = jelement.getAsJsonObject();
			JsonArray objectsArray = jobject.getAsJsonArray("objects");
			for (JsonElement field : objectsArray) {
				FieldValues new_field = new Gson().fromJson(field, FieldValues.class);
				field_list.put(new_field.object_key, new_field);
			}

			// Notify the appliance that something is happened
			;
			((NestDeviceImpl) dev).notifyFrame(dev.getId(),
					field_list.get("shared." + deviceId).value.current_temperature,
					field_list.get("device." + deviceId).value.current_humidity,
					field_list.get("shared." + deviceId).value.target_temperature,
					field_list.get("structure." + structureId).value.away);
		}
	}
}
