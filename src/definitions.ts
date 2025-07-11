export interface GPSSiafesonPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
