require 'pubnub'
	
pubnub = Pubnub.new(
    :publish_key => 'pub-c-ecd86a1b-4ad4-4b48-9d34-8b07cf3d8c3f',
    :subscribe_key => 'sub-c-380453a0-4f40-11e4-b29a-02ee2ddab7fe'
)

class Geofence < ActiveRecord::Base

	reverse_geocoded_by :y_act, :x_act
	after_validation :reverse_geocode, :alarm

	protected

		def alarma
				pubnub.publish(
				    :channel => 'SmartBike',
				    :message => 'ALARMA! Tu bicicleta ha cambiado de posicion'
				)
		end
end
