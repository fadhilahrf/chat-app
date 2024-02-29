import { Injectable } from "@angular/core";
import * as SockJS from 'sockjs-client';
import { Stomp, CompatClient } from '@stomp/stompjs';

@Injectable({ providedIn: 'root' })
export class StompService {
    // sock = new SockJS('/ws');
    stomp = Stomp.over(() => new SockJS('/ws'));

    getStomp(): CompatClient {
        return this.stomp; 
    }

    send(destination: string, headers: any, body: string): void {
        if(this.stomp.connected) {
            this.stomp.send(destination, headers, body);
            return;
        }

        this.stomp.connect({}, ()=>{
            this.stomp.send(destination, headers, body);
        })
    }

    subscribe(topic: string, callback: any): void {
        if(this.stomp.connected) {
            this.stomp.subscribe(topic, callback);
            return;
        }

        this.stomp.connect({}, ()=>{
            this.stomp.subscribe(topic, callback);
        })
    }

    connect(...args: any[]): void {
        this.stomp.connect(...args);
    }
    
}