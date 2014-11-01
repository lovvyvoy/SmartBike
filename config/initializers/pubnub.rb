PUBNUB_CONFIG = {
  'publish_key'   => 'pub-c-ecd86a1b-4ad4-4b48-9d34-8b07cf3d8c3f', 
  'subscribe_key' => 'sub-c-380453a0-4f40-11e4-b29a-02ee2ddab7fe'
}
  
error_callback = lambda do |msg|
  puts "SOMETHING TERRIBLE HAPPENED HERE: #{msg.inspect}"
end
connect_callback = lambda do |msg|
  puts "CONNECTED: #{msg.inspect}"
end

$pubnub = Pubnub.new(
  subscribe_key: PUBNUB_CONFIG['subscribe_key'],
  publish_key: PUBNUB_CONFIG['publish_key'],
  error_callback: error_callback,
  connect_callback: connect_callback,
  logger: Rails.logger
)

$callback = lambda do |envelope|
  Message.create(
    :author => envelope.msg['author'],
    :message => envelope.msg['message'],
    :timetoken => envelope.timetoken
  ) if envelope.msg['author'] && envelope.msg['message']
end
