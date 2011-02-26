package org.webbitserver.netty;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.util.CharsetUtil;
import org.webbitserver.InboundCookieParser;

import java.net.HttpCookie;
import java.net.SocketAddress;
import java.util.*;

public class NettyHttpRequest implements org.webbitserver.HttpRequest {

    private final HttpRequest httpRequest;
    private final MessageEvent messageEvent;
    private final Map<String, Object> data = new HashMap<String, Object>();
    private final Object id;
    private final long timestamp;

    public NettyHttpRequest(MessageEvent messageEvent, HttpRequest httpRequest, Object id, long timestamp) {
        this.messageEvent = messageEvent;
        this.httpRequest = httpRequest;
        this.id = id;
        this.timestamp = timestamp;
    }

    @Override
    public String uri() {
        return httpRequest.getUri();
    }

    @Override
    public String header(String name) {
        return httpRequest.getHeader(name);
    }

    @Override
    public List<String> headers(String name) {
        return httpRequest.getHeaders(name);
    }

    @Override
    public boolean hasHeader(String name) {
        return httpRequest.containsHeader(name);
    }

    @Override
    public List<HttpCookie> cookies() {
        return InboundCookieParser.parse(headers(COOKIE_HEADER));
    }

    @Override
    public HttpCookie cookie(String name) {
        for (HttpCookie cookie : cookies()) {
            if(cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    @Override
    public String cookieValue(String name) {
        HttpCookie cookie = cookie(name);
        return cookie == null ? null : cookie.getValue();
    }

    @Override
    public List<Map.Entry<String, String>> allHeaders() {
        return httpRequest.getHeaders();
    }

    @Override
    public String method() {
        return httpRequest.getMethod().getName();
    }

    @Override
    public String body() {
        return httpRequest.getContent().toString(CharsetUtil.UTF_8); // TODO get charset from request
    }

    @Override
    public Map<String, Object> data() {
        return data;
    }

    @Override
    public Object data(String key) {
        return data.get(key);
    }

    @Override
    public NettyHttpRequest data(String key, Object value) {
        data.put(key, value);
        return this;
    }

    @Override
    public Set<String> dataKeys() {
        return data.keySet();
    }

    @Override
    public SocketAddress remoteAddress() {
        return messageEvent.getRemoteAddress();
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return messageEvent.getRemoteAddress() + " " + httpRequest.getMethod() + " " + httpRequest.getUri();
    }
}
