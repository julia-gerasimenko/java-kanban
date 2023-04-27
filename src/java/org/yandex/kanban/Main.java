package org.yandex.kanban;

import org.yandex.kanban.server.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer(8078).start();
    }
}