require File.expand_path('../boot', __FILE__)
    
require 'rails/all'
# Require the gems listed in Gemfile, including any gems
# you've limited to :test, :development, or :production.
Bundler.require(*Rails.groups) 


module SmartBike
  class Application < Rails::Application
    # Settings in config/environments/* take precedence over those specified here.
    # Application configuration should go into files in config/initializers
    # -- all .rb files in that directory are automatically loaded.
    
    # $pubnub = Pubnub.new(
    #     :publish_key => 'pub-c-ecd86a1b-4ad4-4b48-9d34-8b07cf3d8c3f', 
    #     :subscribe_key => 'sub-c-380453a0-4f40-11e4-b29a-02ee2ddab7fe'
    # )

    # $callback = lambda do |envelope|
    #   Message.create(
    #       :author => envelope.msg['author'],
    #       :message => envelope.msg['message'],
    #       :timetoken => envelope.timetoken
    #   ) if envelope.msg['author'] && envelope.msg['message']
    # end

    # $pubnub.publish(
    #         :channel => 'SmartBike',
    #         :message => 'alerta',
    #         :callback => $callback
    # )

    # Set Time.zone default to the specified zone and make Active Record auto-convert to this zone.
    # Run "rake -D time" for a list of tasks for finding time zone names. Default is UTC.
    # config.time_zone = 'Central Time (US & Canada)'
    config.serve_static_assets = true
    # The default locale is :en and all translations from config/locales/*.rb,yml are auto loaded.
    # config.i18n.load_path += Dir[Rails.root.join('my', 'locales', '*.{rb,yml}').to_s]
    # config.i18n.default_locale = :de
  end
end
