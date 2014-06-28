module TracksHelper

	def google_maps_api_key
		"AIzaSyD5vuFoJirfgRNu5y5HmlNmnb7dIDKjkp4"
	end

	def google_api_url
		"http://maps.googleapis.com/maps/api/js"
	end

	def google_api_access
		"#{google_api_url}?key=#{google_maps_api_key}&amp;libraries=geometry&amp;sensor=false"
	end

end