###
# To build:
#  docker build -t nboumaza/workers .
#
# To run:
#   docker run -t -i -p 7070:70770 nboumaza/workers
###

# Extend vert.x image
FROM vertx/vertx3

ENV VERTICLE_NAME Master
ENV VERTICLE_FILE build/libs/workers.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 7070

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]