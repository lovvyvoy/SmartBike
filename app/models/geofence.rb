class Geofence < ActiveRecord::Base
	
	reverse_geocoded_by :x_act, :y_act
	after_validation :reverse_geocode, :if => :x_act_changed?

end
