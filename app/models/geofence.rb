class Geofence < ActiveRecord::Base
	reverse_geocoded_by :x_act, :y_act,
		:address => :location
	after_validation :reverse_geocode

    def gmaps4rails_address
   	end
end
