
FROM        xxlabaza/alpine
MAINTAINER  Artem Labazin <xxlabaza@gmail.com>

ENV PGDATA /var/lib/postgresql/data

VOLUME /var/lib/postgresql/data

EXPOSE 5432

RUN apk add --no-cache --no-progress \
        postgresql \
        postgresql-contrib \
    && apk add --virtual=build-dependencies --no-cache --no-progress \
        curl \
     && curl -o /usr/local/bin/gosu -sSL "https://github.com/tianon/gosu/releases/download/1.10/gosu-amd64" \
    && apk del build-dependencies \
    && chmod +x /usr/local/bin/gosu

COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY schema.sql /docker-entrypoint-initdb.d/

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["postgres"]
