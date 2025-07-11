import { registerPlugin } from '@capacitor/core';

import type { GPSSiafesonPlugin } from './definitions';

const GPSSiafeson = registerPlugin<GPSSiafesonPlugin>('GPSSiafeson', {
  web: () => import('./web').then((m) => new m.GPSSiafesonWeb()),
});

export * from './definitions';
export { GPSSiafeson };
