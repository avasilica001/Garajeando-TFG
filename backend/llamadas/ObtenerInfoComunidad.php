<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='GET'){
                if (isset($_GET['IdComunidad'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerUsuariosAceptar($_GET['IdComunidad']);
                        $respuesta['UsuariosAceptar'] = $resultado;
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "OK";

				$resultado2 = $bbdd->obtenerCodInvitacionComunidad($_GET['IdComunidad']);
                        	$respuesta['CodInvitacion'] = $resultado2;

				$resultado3 = $bbdd->obtenerUsuariosComunidad($_GET['IdComunidad']);
                                $respuesta['Usuarios'] = $resultado3;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No hay comunidades para mostrar.";
                }
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>
