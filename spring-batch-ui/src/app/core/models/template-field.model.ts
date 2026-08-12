export interface TemplateField {

  id: number;

  name: string;

  fieldType: FieldType;

  position: number;

  length: number | null;

  required: boolean;

  defaultValue: string | null;

  orderIndex: number | null;

  transformations: FieldTransformation[];

}

export interface TemplateFieldRequest {

  name: string;

  fieldType: FieldType;

  position: number;

  length?: number | null;

  required: boolean;

  defaultValue?: string | null;

  orderIndex?: number | null;

}

export interface FieldTransformation {

  id: number;

  transformerName: string;

  orderIndex?: number;

}

export enum FieldType {

  STRING = 'STRING',

  INTEGER = 'INTEGER',

  LONG = 'LONG',

  DECIMAL = 'DECIMAL',

  DATE = 'DATE',

  BOOLEAN = 'BOOLEAN'

}
