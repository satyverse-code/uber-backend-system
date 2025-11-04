{{- define "notification.name" -}}
{{- default .Chart.Name .Values.nameOverride -}}
{{- end -}}

{{- define "notification.fullname" -}}
{{ printf "%s-%s" (include "notification.name" .) .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "notification.labels" -}}
app: {{ include "notification.name" . }}
chart: "{{ .Chart.Name }}-{{ .Chart.Version }}"
release: {{ .Release.Name }}
heritage: {{ .Release.Service }}
{{- end -}}

{{- define "notification.serviceAccountName" -}}
{{ include "notification.fullname" . }}-sa
{{- end -}}
