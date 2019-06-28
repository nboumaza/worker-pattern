# coding: utf-8
require 'net/http'
require 'uri'
require 'json'

uri = URI.parse("http://localhost:7070/jobs")
header = {'Content-Type': 'application/json'}

coffee_types = ['americano', 'latte', 'cappucino', 'mocha']
coffee_size = ['small', 'large', 'medium']
customers = ['Coco', 'Zazie', 'Ztune', 'Sarah', 'Marco', 'François', 'Kaly', 'Bonnie']

http = Net::HTTP.new(uri.host, uri.port)

2.times do
  3.times do
    #Iteration 1: keep the job simple
    job = {
      customerId: customers.sample,
      coffee: coffee_types.sample,
      size: coffee_size.sample
    }

    request = Net::HTTP::Post.new(uri.request_uri, header)
    request.body = job.to_json
    response = http.request(request)
  end
  sleep 2
end