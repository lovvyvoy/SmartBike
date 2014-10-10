json.array!(@viajes) do |viaje|
  json.extract! viaje, :id, :id_viaje, :user_id, :distancia, :tiempo, :fecha
  json.url viaje_url(viaje, format: :json)
end
