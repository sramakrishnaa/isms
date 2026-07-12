import { Component, output } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  readonly navigate = output<void>();
  expandedMenus: Record<string, boolean> = {};
  expanded = false;

  readonly navigationItems = [
  {
    label: 'Dashboard',
    icon: 'dashboard',
    route: '/dashboard',
  },
  {
    label: 'Inventory',
    icon: 'inventory',
    children: [
      {
        label: 'Products',
        icon: 'inventory',
        route: '/products',
      },
      {
        label: 'Categories',
        icon: 'category',
        route: '/categories',
      },
    ],
  },
  {
    label: 'Administration',
    icon: 'admin_panel_settings',
    children: [
      {
        label: 'Users',
        icon: 'group',
        route: '/users',
      },
      {
        label: 'Roles',
        icon: 'security',
        route: '/roles',
      },
    ],
  },
];

  toggleMenu(label: string) {
    this.expandedMenus[label] = !this.expandedMenus[label];
  }
  isExpanded(label: string): boolean {
    return this.expandedMenus[label] || false;
  }
}
