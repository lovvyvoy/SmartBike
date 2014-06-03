json.array!(@geofences) do |geofence|
  json.extract! geofence, :id, :user_id, :x_act, :y_act, :x_al, :y_al, :alarm
  json.url geofence_url(geofence, format: :json)
end
