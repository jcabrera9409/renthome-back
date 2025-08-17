import { Component, HostListener, OnInit } from '@angular/core';
import { RouterOutlet, RouterModule, Router } from '@angular/router';
import { Building2Icon, CreditCardIcon, FileTextIcon, HouseIcon, LayoutDashboardIcon, LogOutIcon, LucideAngularModule, MenuIcon, ReceiptIcon, UsersIcon, XIcon } from 'lucide-angular';
import { CasaService } from '../../_service/casa.service';
import { CasaDTO } from '../../_model/dto';
import { NotificationService } from '../../_service/notification.service';
import { Message } from '../../_model/message';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, LucideAngularModule, RouterModule, CommonModule],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit {
  readonly menuIcon = MenuIcon
  readonly xIcon = XIcon
  readonly buildingIcon = Building2Icon
  readonly logoutIcon = LogOutIcon
  readonly layoutDashboardIcon = LayoutDashboardIcon
  readonly creditCardIcon = CreditCardIcon
  readonly houseIcon = HouseIcon
  readonly fileTextIcon = FileTextIcon
  readonly usersIcon = UsersIcon
  readonly receiptIcon = ReceiptIcon

  isOpenSidebar = false;
  isDesktopCollapse = false;
  isDesktop = window.innerWidth >= 1024;  

  casas: CasaDTO[] = [];

  constructor(
    private router: Router,
    private casaService: CasaService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.listarCasas();
  }

  listarCasas() {
    this.casaService.listarMisCasas().subscribe({
      next: (data) => {
        if (data.success) {
          this.casas = data.data;
          this.casaService.setCasas(this.casas);
        } else {
          this.casaService.setCasaSeleccionada(null);
          this.notificationService.setMessageChange(
            Message.error('No se encontraron casas')
          );
        }

      },
      error: (error) => {
        this.casaService.setCasaSeleccionada(null);
        this.notificationService.setMessageChange(
          Message.error('Error al cargar las casas')
        );
      }
    });
  }

  toggleSidebar() {
    if (this.isDesktop) {
      this.isDesktopCollapse = !this.isDesktopCollapse;
    } else {
      this.isOpenSidebar = !this.isOpenSidebar;
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    this.isDesktop = (event.target as Window).innerWidth >= 1024;
  }

  getCssTabPage(tabName: string): string {
    const page = this.router.url.replaceAll('/admin', '').replaceAll('/', '');
    if (page === tabName) {
      return "bg-indigo-100 text-indigo-700";
    }

    return "text-gray-600 hover:bg-gray-100"
  }

  get getSidebarClass() {
    if (this.isDesktop) {
      if (this.isDesktopCollapse) {
        return '-translate-x-full';
      } else {
        return 'translate-x-0';
      }
    } else {
      if (this.isOpenSidebar) {
        return '';
      } else {
        return '-translate-x-full';
      }
    }
  }

  get getMainContentClass() {
    if (this.isDesktop) {
      if (!this.isDesktopCollapse) {
        return 'open';
      } else {
        return '';
      }
    }
    return '';
  }

  get getOverlayClass() {
    if (this.isDesktop) {
      return 'hidden';
    }
    return this.isOpenSidebar ? '' : 'hidden';
  }

  get isMenuIconVisible() {
    if (this.isDesktop) {
      return !this.isDesktopCollapse;
    } else {
      return this.isOpenSidebar;
    }
  }

  get isXIconVisible() {
    if (this.isDesktop) {
      return this.isDesktopCollapse;
    } else {
      return !this.isOpenSidebar;
    }
  }

}
