import { Injectable } from '@angular/core';
import { Message } from '../_model/message';
import { Subject } from 'rxjs/internal/Subject';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private messageChange: Subject<Message> = new Subject<Message>();

  constructor() { }

  getMessageChange() {
    return this.messageChange.asObservable();
  }

  setMessageChange(message: Message) {
    this.messageChange.next(message);
  }
}
