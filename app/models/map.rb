class Map < ActiveRecord::Base

	reverse_geocoded_by :latitud, :longitude,
		:address => :location
	after_validation :reverse_geocoded

end
