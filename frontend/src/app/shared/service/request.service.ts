import { HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "src/environments/environment";
import { ApiResourceEnum } from "../enum/api-resource.enum";

@Injectable({
    providedIn: 'root'
  })
  export class RequestService {
  
    private _resource: ApiResourceEnum;
  
    constructor(resource: ApiResourceEnum) {
      this._resource = resource;
    }
  
    static get baseUrl(): string {
      const hostname: string = environment.api.protocol + '://' + environment.api.hostname;
      const port: string = environment.api.port ? `:${environment.api.port}` : '';
      const context: string = environment.api.context ? `/${environment.api.context}` : '';
  
      return hostname + port + context;
    }
  
    static get baseHttpOptions(): { headers: HttpHeaders } {
      return {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      };
    }
  
    protected _getUrl(id?: number): string {
      if (id)
        return `${RequestService.baseUrl}/${this._resource}/${id}`;
      else
        return `${RequestService.baseUrl}/${this._resource}`;
    }
  }
  