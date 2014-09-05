class Geofence < ActiveRecord::Base
	reverse_geocoded_by :x_act, :y_act do |obj, results|
		if geo = results.first
			obj.city = geo.city
			obj.zipcode = geo.postal_code
			obj.country = geo.country_code
		end
	end

	after_validation :fetch_address # auto-fetch address
end
