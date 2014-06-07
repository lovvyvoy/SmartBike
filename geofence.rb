# Haversine method
def distance a, b
  rad_per_deg = Math::PI/180  # PI / 180
  rkm = 6371                  # Earth radius in kilometers
  rm = rkm * 1000             # Radius in meters

  dlon_rad = (b[1]-a[1]) * rad_per_deg  # Delta, converted to rad
  dlat_rad = (b[0]-a[0]) * rad_per_deg

  lat1_rad, lon1_rad = a.map! {|i| i * rad_per_deg }
  lat2_rad, lon2_rad = b.map! {|i| i * rad_per_deg }

  a = Math.sin(dlat_rad/2)**2 + Math.cos(lat1_rad) * Math.cos(lat2_rad) * Math.sin(dlon_rad/2)**2
  c = 2 * Math.asin(Math.sqrt(a))

  rm * c # Delta in meters
end

a = 0
while true
	pos_actual = PEDIR POSICION
	¿ALARMA ACTIVADA?
	if ALARMA ACTIVADA
		if a == 0
			pos_alarma = pos_actual
			a = 1
    end
		if (distance pos_alarma, pos_actual) > 5
			MENSAJE ALERTA
    end
	sleep(5000)
end
