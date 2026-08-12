import {
  TemplateField
} from './template-field.model';

import {
  Destination
} from './destination.model';

export interface Template {

  id: number;

  name: string;

  description: string | null;

  fileType: FileType;

  separator: string | null;

  encoding: string;

  skipEmptyLines: boolean;

  onError: OnErrorBehavior;

  active: boolean;

  createdAt: string;

  updatedAt: string;

  fields?: TemplateField[];

  destinations?: Destination[];

}

export interface TemplateRequest {

  name: string;

  description?: string | null;

  fileType: FileType;

  separator?: string | null;

  encoding: string;

  skipEmptyLines: boolean;

  onError: OnErrorBehavior;

  active: boolean;

}

export enum FileType {

  DELIMITED = 'DELIMITED',

  FIXED_WIDTH = 'FIXED_WIDTH'

}

export enum OnErrorBehavior {

  CONTINUE = 'CONTINUE',

  STOP = 'STOP'

}
