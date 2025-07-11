import { WebPlugin } from '@capacitor/core';

import type { GPSSiafesonPlugin } from './definitions';

export class GPSSiafesonWeb extends WebPlugin implements GPSSiafesonPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
