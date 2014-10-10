json.array!(@logros) do |logro|
  json.extract! logro, :id, :id_logro, :meta, :int_multiuso, :logrado, :nombre, :date_inicial, :date_final
  json.url logro_url(logro, format: :json)
end
