import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService } from '../../_service/notification.service';
import { CommonModule } from '@angular/common';
import { Message } from '../../_model/message';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.css'
})
export class NotificationComponent implements OnInit, OnDestroy {
  currentMessage: Message | null = null;
  isVisible: boolean = false;
  private messageSubscription: Subscription = new Subscription();
  private hideTimer: any;

  constructor(
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.messageSubscription = this.notificationService.getMessageChange().subscribe({
      next: (message: Message) => {
        this.showNotification(message);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
    }
    if (this.hideTimer) {
      clearTimeout(this.hideTimer);
    }
  }

  private showNotification(message: Message) {
    // Si hay un timer activo, lo cancelamos
    if (this.hideTimer) {
      clearTimeout(this.hideTimer);
    }

    // Establecemos el nuevo mensaje y lo mostramos
    this.currentMessage = message;
    this.isVisible = true;

    // Configuramos el timer para ocultar después de 3 segundos
    this.hideTimer = setTimeout(() => {
      this.hideNotification();
    }, 3000);
  }

  private hideNotification() {
    this.isVisible = false;
    // Esperamos un poco para limpiar el mensaje después de que se oculte
    setTimeout(() => {
      this.currentMessage = null;
    }, 300); // 300ms para permitir animación de salida
  }

  getNotificationIcon(): string {
    if (!this.currentMessage) return '';
    
    switch (this.currentMessage.status.toLowerCase()) {
      case 'success':
        return '✅';
      case 'error':
        return '❌';
      case 'warning':
        return '⚠️';
      case 'info':
        return 'ℹ️';
      default:
        return 'ℹ️';
    }
  }

  getNotificationClass(): string {
    if (!this.currentMessage) return '';
    
    switch (this.currentMessage.status.toLowerCase()) {
      case 'success':
        return 'notification-success';
      case 'error':
        return 'notification-error';
      case 'warning':
        return 'notification-warning';
      case 'info':
        return 'notification-info';
      default:
        return 'notification-info';
    }
  }
}
