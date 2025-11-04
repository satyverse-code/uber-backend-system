{{- define "gateway.name" -}}
{{- default .Chart.Name .Values.nameOverride -}}
{{- end -}}

{{- define "gateway.fullname" -}}
{{ printf "%s-%s" (include "gateway.name" .) .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "gateway.labels" -}}
app: {{ include "gateway.name" . }}
chart: "{{ .Chart.Name }}-{{ .Chart.Version }}"
release: {{ .Release.Name }}
heritage: {{ .Release.Service }}
{{- end -}}

{{- define "gateway.serviceAccountName" -}}
{{ include "gateway.fullname" . }}-sa
{{- end -}}
