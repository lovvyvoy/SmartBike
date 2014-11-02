class Geofence < ActiveRecord::Base

	reverse_geocoded_by :y_act, :x_act
	after_validation :reverse_geocode, :alarma

end
