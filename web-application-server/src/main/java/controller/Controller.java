package controller;

import http.HttpRequest_book;
import http.HttpResponse_book;


public interface Controller{
	void service(HttpRequest_book request, HttpResponse_book response);
}
